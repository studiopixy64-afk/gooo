package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.DefaultStudioSeed
import com.example.data.model.ServiceCategory
import com.example.data.model.StudioService
import com.example.ui.components.ServiceCatalogCard
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.IvoryCream
import com.example.ui.theme.MutedSilver
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateVelvet
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.UiState

@Composable
fun ServicesScreen(
    servicesState: UiState<List<StudioService>>,
    filteredServices: List<StudioService>,
    searchQuery: String,
    selectedCategory: ServiceCategory?,
    hindiMode: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (ServiceCategory?) -> Unit,
    onAddToCart: (StudioService) -> Unit,
    onOrderService: (StudioService) -> Unit
) {
    // If Firestore is still empty before first admin sync, gracefully preview starter catalog
    val displayList = when {
        servicesState is UiState.Success && servicesState.data.isEmpty() -> {
            val starter = DefaultStudioSeed.starterServices().map { (id, s) -> s.copy(id = id) }
            val q = searchQuery.trim().lowercase()
            starter.filter { svc ->
                val catOk = selectedCategory == null || svc.categoryEnum == selectedCategory
                val queryOk = q.isBlank() || svc.name.lowercase().contains(q) || svc.nameHindi.lowercase().contains(q)
                catOk && queryOk
            }
        }
        else -> filteredServices.filter { it.active }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = if (hindiMode) "सेवाएं व रेट लिस्ट • Services & Rates" else "Services & Rate List",
                style = MaterialTheme.typography.headlineMedium,
                color = WarmGold
            )
            Text(
                text = "Live studio pricing in Indian Rupees (₹)",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSilver
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        if (hindiMode) "फ्रेम, पासपोर्ट, वेडिंग खोजें..." else "Search frames, passport, wedding, prints...",
                        color = MutedSilver
                    )
                },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = ChampagneGold)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear search", tint = MutedSilver)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = SubtleDivider,
                    focusedContainerColor = SlateVelvet,
                    unfocusedContainerColor = SlateVelvet,
                    focusedTextColor = IvoryCream,
                    unfocusedTextColor = IvoryCream
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_services_input")
            )
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onSelectCategory(null) },
                    label = { Text(if (hindiMode) "सभी / All" else "All Services") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChampagneGold,
                        selectedLabelColor = ObsidianBlack,
                        containerColor = SlateVelvet,
                        labelColor = IvoryCream
                    ),
                    modifier = Modifier.testTag("filter_chip_all")
                )
            }
            items(ServiceCategory.entries) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        onSelectCategory(if (selectedCategory == category) null else category)
                    },
                    label = {
                        Text(if (hindiMode) category.hindiLabel else category.displayName)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChampagneGold,
                        selectedLabelColor = ObsidianBlack,
                        containerColor = SlateVelvet,
                        labelColor = IvoryCream
                    ),
                    modifier = Modifier.testTag("filter_chip_${category.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when {
            servicesState is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ChampagneGold)
                }
            }
            displayList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.SearchOff,
                            contentDescription = null,
                            tint = ChampagneGold.copy(alpha = 0.6f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching services found",
                            style = MaterialTheme.typography.titleMedium,
                            color = IvoryCream
                        )
                        Text(
                            text = "Try clearing your search filter or choosing another category.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSilver
                        )
                    }
                }
            }
            else -> {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val columns = if (maxWidth >= 680.dp) 2 else 1
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = displayList,
                            key = { it.id.ifBlank { it.name } }
                        ) { service ->
                            ServiceCatalogCard(
                                service = service,
                                hindiMode = hindiMode,
                                onCardClick = { onOrderService(service) },
                                onAddToCart = { onAddToCart(service) },
                                onWhatsAppOrder = { onOrderService(service) }
                            )
                        }
                    }
                }
            }
        }
    }
}
