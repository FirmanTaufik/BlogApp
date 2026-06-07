package com.time.yourguideapp.presentation.love

data class PopularPlaceSeed(
    val pageTitle: String,
    val fallbackLocation: String,
    val continent: PopularPlaceContinent,
)

data class PopularPlace(
    val title: String,
    val location: String,
    val description: String,
    val imageUrl: String,
    val sourceUrl: String,
    val continent: PopularPlaceContinent,
)

enum class PopularPlaceContinent {
    Asia,
    Europe,
    Africa,
    NorthAmerica,
    SouthAmerica,
    Oceania,
}

data class PopularPlacesUiState(
    val places: List<PopularPlace> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

val popularPlaceSeeds = listOf(
    PopularPlaceSeed("Eiffel_Tower", "Paris, France", PopularPlaceContinent.Europe),
    PopularPlaceSeed("Taj_Mahal", "Agra, India", PopularPlaceContinent.Asia),
    PopularPlaceSeed("Great_Wall_of_China", "China", PopularPlaceContinent.Asia),
    PopularPlaceSeed("Machu_Picchu", "Cusco Region, Peru", PopularPlaceContinent.SouthAmerica),
    PopularPlaceSeed("Colosseum", "Rome, Italy", PopularPlaceContinent.Europe),
    PopularPlaceSeed("Petra", "Ma'an, Jordan", PopularPlaceContinent.Asia),
    PopularPlaceSeed("Christ_the_Redeemer_(statue)", "Rio de Janeiro, Brazil", PopularPlaceContinent.SouthAmerica),
    PopularPlaceSeed("Santorini", "Cyclades, Greece", PopularPlaceContinent.Europe),
    PopularPlaceSeed("Angkor_Wat", "Siem Reap, Cambodia", PopularPlaceContinent.Asia),
    PopularPlaceSeed("Sagrada_Familia", "Barcelona, Spain", PopularPlaceContinent.Europe),
    PopularPlaceSeed("Stonehenge", "Wiltshire, England", PopularPlaceContinent.Europe),
    PopularPlaceSeed("Statue_of_Liberty", "New York City, United States", PopularPlaceContinent.NorthAmerica),
    PopularPlaceSeed("Pyramids_of_Giza", "Giza, Egypt", PopularPlaceContinent.Africa),
    PopularPlaceSeed("Burj_Khalifa", "Dubai, United Arab Emirates", PopularPlaceContinent.Asia),
    PopularPlaceSeed("Sydney_Opera_House", "Sydney, Australia", PopularPlaceContinent.Oceania),
    PopularPlaceSeed("Niagara_Falls", "Ontario, Canada / New York, United States", PopularPlaceContinent.NorthAmerica),
    PopularPlaceSeed("Grand_Canyon", "Arizona, United States", PopularPlaceContinent.NorthAmerica),
    PopularPlaceSeed("Mount_Fuji", "Honshu, Japan", PopularPlaceContinent.Asia),
    PopularPlaceSeed("Acropolis_of_Athens", "Athens, Greece", PopularPlaceContinent.Europe),
    PopularPlaceSeed("Tower_Bridge", "London, England", PopularPlaceContinent.Europe),
    PopularPlaceSeed("Louvre", "Paris, France", PopularPlaceContinent.Europe),
    PopularPlaceSeed("Neuschwanstein_Castle", "Bavaria, Germany", PopularPlaceContinent.Europe),
    PopularPlaceSeed("Chichen_Itza", "Yucatan, Mexico", PopularPlaceContinent.NorthAmerica),
    PopularPlaceSeed("Borobudur", "Central Java, Indonesia", PopularPlaceContinent.Asia),
    PopularPlaceSeed("Bali", "Indonesia", PopularPlaceContinent.Asia),
    PopularPlaceSeed("Hagia_Sophia", "Istanbul, Turkey", PopularPlaceContinent.Asia),
    PopularPlaceSeed("Victoria_Falls", "Zambia / Zimbabwe", PopularPlaceContinent.Africa),
    PopularPlaceSeed("Serengeti_National_Park", "Tanzania", PopularPlaceContinent.Africa),
)
