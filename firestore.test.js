const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const CUSTOMER_UID = "customer_alice_123";
const OTHER_UID = "customer_bob_456";
const ADMIN_UID = "studio_admin_999";
const ADMIN_EMAIL = "studiopixy64@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validServicePayload(now = new Date(Date.now() - 2000)) {
  return {
    name: "8x10 Photo Frame",
    nameHindi: "8x10 फोटो फ्रेम",
    category: "PHOTO_FRAMES",
    priceInr: 450,
    unitLabel: "per frame",
    description: "Matte gold and obsidian wooden frame with clear glass.",
    imageKey: "drawable:img_frames_showcase",
    popular: true,
    active: true,
    sortOrder: 3,
    createdAt: now,
    updatedAt: now,
  };
}

function validBusinessConfigPayload(now = new Date(Date.now() - 2000)) {
  return {
    businessName: "Studio Pixy",
    tagline: "Capturing Timeless Moments",
    whatsappNumber: "+919876543210",
    phoneNumber: "+919876543210",
    address: "Shop 12, Main Market Road, New Delhi",
    email: "studiopixy64@gmail.com",
    openingHours: "Mon-Sat 9:30 AM - 8:30 PM",
    googleMapsQuery: "Studio Pixy Photography Studio",
    offerBadge: "FESTIVE 15% OFF",
    offerDescription: "15% off on 16x20 Gold Frames & Wedding Albums.",
    aboutDescription: "Studio Pixy is a modern photography and fine printing studio.",
    createdAt: now,
    updatedAt: now,
  };
}

function validOrderPayload(uid, now = new Date(Date.now() - 2000)) {
  return {
    userId: uid,
    customerEmail: "alice@example.com",
    customerName: "Aarav Sharma",
    customerPhone: "+919811122233",
    requirements: "Need urgent pickup by 5 PM",
    items: [
      {
        serviceId: "svc_passport",
        serviceName: "Urgent Passport Photo",
        unitPriceInr: 150,
        quantity: 2,
        lineTotalInr: 300,
      },
    ],
    totalQuantity: 2,
    totalAmountInr: 300,
    status: "PENDING",
    adminNotes: "",
    createdAt: now,
    updatedAt: now,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: denied across all collections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("services").get());
  await assertFails(unauthDb.collection("business_config").doc("studio_profile").get());
  await assertFails(unauthDb.collection("orders").where("userId", "==", CUSTOMER_UID).get());
  await assertFails(unauthDb.collection("reviews").get());
  await assertFails(unauthDb.collection("users").doc(CUSTOMER_UID).get());
});

test("Services catalog: customers can read, only verified admin can mutate", async () => {
  const adminDb = testEnv
    .authenticatedContext(ADMIN_UID, { email: ADMIN_EMAIL, email_verified: true })
    .firestore();
  const spoofedAdminDb = testEnv
    .authenticatedContext(OTHER_UID, { email: ADMIN_EMAIL, email_verified: false })
    .firestore();
  const customerDb = testEnv.authenticatedContext(CUSTOMER_UID).firestore();

  // Admin creates service (no `id` field inside body)
  await assertSucceeds(
    adminDb.collection("services").doc("frame_8x10").set(validServicePayload())
  );

  // Spoofed unverified email rejected
  await assertFails(
    spoofedAdminDb.collection("services").doc("frame_spoof").set(validServicePayload())
  );

  // Customer cannot create/update/delete services
  await assertFails(
    customerDb.collection("services").doc("frame_cust").set(validServicePayload())
  );
  await assertFails(
    customerDb.collection("services").doc("frame_8x10").update({ priceInr: 1 })
  );

  // Customer can read/list services
  await assertSucceeds(customerDb.collection("services").get());

  // Admin can update price while preserving createdAt
  await assertSucceeds(
    adminDb.collection("services").doc("frame_8x10").update({
      priceInr: 550,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Shadow update with ghost field rejected
  await assertFails(
    adminDb.collection("services").doc("frame_8x10").update({
      priceInr: 600,
      ghostField: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Business config: customer reads studio_profile, admin updates contact info", async () => {
  const adminDb = testEnv
    .authenticatedContext(ADMIN_UID, { email: ADMIN_EMAIL, email_verified: true })
    .firestore();
  const customerDb = testEnv.authenticatedContext(CUSTOMER_UID).firestore();

  await assertSucceeds(
    adminDb.collection("business_config").doc("studio_profile").set(validBusinessConfigPayload())
  );

  await assertSucceeds(customerDb.collection("business_config").doc("studio_profile").get());
  await assertFails(customerDb.collection("business_config").get());
  await assertFails(
    customerDb.collection("business_config").doc("studio_profile").update({
      whatsappNumber: "+910000000000",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  await assertSucceeds(
    adminDb.collection("business_config").doc("studio_profile").update({
      whatsappNumber: "+919999888877",
      openingHours: "Daily 9 AM - 9 PM",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Orders: owner isolation, filtered list query, and admin status transitions", async () => {
  const aliceDb = testEnv.authenticatedContext(CUSTOMER_UID).firestore();
  const bobDb = testEnv.authenticatedContext(OTHER_UID).firestore();
  const adminDb = testEnv
    .authenticatedContext(ADMIN_UID, { email: ADMIN_EMAIL, email_verified: true })
    .firestore();

  // Customer creates own order starting as PENDING
  await assertSucceeds(
    aliceDb.collection("orders").doc("order_1").set(validOrderPayload(CUSTOMER_UID))
  );

  // Reject spoofing another userId or pre-confirming status
  await assertFails(
    aliceDb.collection("orders").doc("order_bad_uid").set(validOrderPayload(OTHER_UID))
  );
  await assertFails(
    aliceDb.collection("orders").doc("order_bad_status").set({
      ...validOrderPayload(CUSTOMER_UID),
      status: "COMPLETED",
    })
  );

  // Filtered query succeeds; unfiltered customer list query fails; Bob cannot read Alice's order
  await assertSucceeds(
    aliceDb.collection("orders").where("userId", "==", CUSTOMER_UID).get()
  );
  await assertFails(aliceDb.collection("orders").get());
  await assertFails(bobDb.collection("orders").doc("order_1").get());
  await assertFails(
    aliceDb.collection("orders").doc("order_1").update({
      status: "COMPLETED",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Admin can list all orders and update status
  await assertSucceeds(adminDb.collection("orders").get());
  await assertSucceeds(
    adminDb.collection("orders").doc("order_1").update({
      status: "CONFIRMED",
      adminNotes: "Ready at 4 PM",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Users PII & Reviews & Admins lock", async () => {
  const aliceDb = testEnv.authenticatedContext(CUSTOMER_UID).firestore();
  const bobDb = testEnv.authenticatedContext(OTHER_UID).firestore();
  const adminDb = testEnv
    .authenticatedContext(ADMIN_UID, { email: ADMIN_EMAIL, email_verified: true })
    .firestore();
  const now = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("users").doc(CUSTOMER_UID).set({
      userId: CUSTOMER_UID,
      displayName: "Alice",
      email: "alice@example.com",
      phone: "+919811122233",
      notes: "",
      orderCount: 1,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Bob cannot read Alice PII or list /users
  await assertFails(bobDb.collection("users").doc(CUSTOMER_UID).get());
  await assertFails(bobDb.collection("users").get());

  // Admin can list and view customer profiles
  await assertSucceeds(adminDb.collection("users").get());

  // Self-promotion to /admins rejected
  await assertFails(
    aliceDb.collection("admins").doc(CUSTOMER_UID).set({ email: "alice@example.com" })
  );

  // Reviews: Alice posts review, Bob reads it, Bob cannot delete Alice's review
  await assertSucceeds(
    aliceDb.collection("reviews").doc("rev_1").set({
      userId: CUSTOMER_UID,
      authorName: "Alice",
      rating: 5,
      serviceUsed: "Wedding Photography",
      comment: "Amazing gold frame quality and fast delivery!",
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(bobDb.collection("reviews").get());
  await assertFails(bobDb.collection("reviews").doc("rev_1").delete());
});
