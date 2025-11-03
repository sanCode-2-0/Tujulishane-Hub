// County and Sub-County data for Kenya
const kenyaLocations = {
  Mombasa: ["Changamwe", "Jomvu", "Kisauni", "Likoni", "Mvita", "Nyali"],
  Kwale: ["Kinango", "Lunga Lunga", "Msambweni", "Matuga"],
  Kilifi: [
    "Ganze",
    "Kaloleni",
    "Kilifi North",
    "Kilifi South",
    "Magarini",
    "Malindi",
    "Rabai",
  ],
  "Tana River": ["Bura", "Galole", "Garsen"],
  Lamu: ["Lamu East", "Lamu West"],
  "Taita-Taveta": ["Mwatate", "Taveta", "Voi", "Wundanyi"],
  Garissa: [
    "Daadab",
    "Fafi",
    "Garissa Township",
    "Hulugho",
    "Ijara",
    "Lagdera",
    "Balambala",
  ],
  Wajir: [
    "Eldas",
    "Tarbaj",
    "Wajir East",
    "Wajir North",
    "Wajir South",
    "Wajir West",
  ],
  Mandera: [
    "Banissa",
    "Lafey",
    "Mandera East",
    "Mandera North",
    "Mandera South",
    "Mandera West",
  ],
  Marsabit: ["Laisamis", "Moyale", "North Horr", "Saku"],
  Isiolo: ["Isiolo", "Merti", "Garbatulla"],
  Meru: [
    "Buuri",
    "Igembe Central",
    "Igembe North",
    "Igembe South",
    "Imenti Central",
    "Imenti North",
    "Imenti South",
    "Tigania East",
    "Tigania West",
  ],
  "Tharaka-Nithi": [
    "Tharaka North",
    "Tharaka South",
    "Chuka",
    "Igambang'ombe",
    "Maara",
    "Chiakariga",
    "Muthambi",
  ],
  Embu: ["Manyatta", "Mbeere North", "Mbeere South", "Runyenjes"],
  Kitui: [
    "Kitui West",
    "Kitui Central",
    "Kitui Rural",
    "Kitui South",
    "Kitui East",
    "Mwingi North",
    "Mwingi West",
    "Mwingi Central",
  ],
  Machakos: [
    "Kathiani",
    "Machakos Town",
    "Masinga",
    "Matungulu",
    "Mavoko",
    "Mwala",
    "Yatta",
  ],
  Makueni: [
    "Kaiti",
    "Kibwezi West",
    "Kibwezi East",
    "Kilome",
    "Makueni",
    "Mbooni",
  ],
  Nyandarua: ["Kinangop", "Kipipiri", "Ndaragwa", "Ol-Kalou", "Ol Joro Orok"],
  Nyeri: [
    "Kieni East",
    "Kieni West",
    "Mathira East",
    "Mathira West",
    "Mukurweini",
    "Nyeri Town",
    "Othaya",
    "Tetu",
  ],
  Kirinyaga: [
    "Kirinyaga Central",
    "Kirinyaga East",
    "Kirinyaga West",
    "Mwea East",
    "Mwea West",
  ],
  "Murang'a": [
    "Gatanga",
    "Kahuro",
    "Kandara",
    "Kangema",
    "Kigumo",
    "Kiharu",
    "Mathioya",
    "Murang'a South",
  ],
  Kiambu: [
    "Gatundu North",
    "Gatundu South",
    "Githunguri",
    "Juja",
    "Kabet",
    "Kiambaa",
    "Kiambu",
    "Kikuyu",
    "Limuru",
    "Ruiru",
    "Thika Town",
    "Lari",
  ],
  Turkana: [
    "Loima",
    "Turkana Central",
    "Turkana East",
    "Turkana North",
    "Turkana South",
  ],
  "West Pokot": ["Central Pokot", "North Pokot", "Pokot South", "West Pokot"],
  Samburu: ["Samburu East", "Samburu North", "Samburu West"],
  "Trans-Nzoia": ["Cherangany", "Endebess", "Kiminini", "Kwanza", "Saboti"],
  "Uasin Gishu": ["Ainabkoi", "Kapseret", "Kesses", "Moiben", "Soy", "Turbo"],
  "Elgeyo-Marakwet": [
    "Keiyo North",
    "Keiyo South",
    "Marakwet East",
    "Marakwet West",
  ],
  Nandi: ["Aldai", "Chesumei", "Emgwen", "Mosop", "Nandi Hills", "Tindiret"],
  Baringo: [
    "Baringo Central",
    "Baringo North",
    "Baringo South",
    "Eldama Ravine",
    "Mogotio",
    "Tiaty",
  ],
  Laikipia: [
    "Laikipia Central",
    "Laikipia East",
    "Laikipia North",
    "Laikipia West",
    "Nyahururu",
  ],
  Nakuru: [
    "Bahati",
    "Gilgil",
    "Kuresoi North",
    "Kuresoi South",
    "Molo",
    "Naivasha",
    "Nakuru Town East",
    "Nakuru Town West",
    "Njoro",
    "Rongai",
    "Subukia",
  ],
  Narok: [
    "Narok East",
    "Narok North",
    "Narok South",
    "Narok West",
    "Transmara East",
    "Transmara West",
  ],
  Kajiado: [
    "Isinya",
    "Kajiado Central",
    "Kajiado North",
    "Loitokitok",
    "Mashuuru",
  ],
  Kericho: [
    "Ainamoi",
    "Belgut",
    "Bureti",
    "Kipkelion East",
    "Kipkelion West",
    "Soin/Sigowet",
  ],
  Bomet: ["Bomet Central", "Bomet East", "Chepalungu", "Konoin", "Sotik"],
  Kakamega: [
    "Butere",
    "Kakamega Central",
    "Kakamega East",
    "Kakamega North",
    "Kakamega South",
    "Khwisero",
    "Lugari",
    "Lukuyani",
    "Lurambi",
    "Matete",
    "Mumias",
    "Mutungu",
    "Navakholo",
  ],
  Vihiga: ["Emuhaya", "Hamisi", "Luanda", "Sabatia", "Vihiga"],
  Bungoma: [
    "Bumula",
    "Kabuchai",
    "Kanduyi",
    "Kimilili",
    "Mt Elgon",
    "Sirisia",
    "Tongaren",
    "Webuye East",
    "Webuye West",
  ],
  Busia: [
    "Budalangi",
    "Butula",
    "Funyula",
    "Nambele",
    "Teso North",
    "Teso South",
  ],
  Siaya: ["Alego Usonga", "Bondo", "Gem", "Rarieda", "Ugenya", "Unguja"],
  Kisumu: [
    "Kisumu Central",
    "Kisumu East",
    "Kisumu West",
    "Muhoroni",
    "Nyakach",
    "Nyando",
    "Seme",
  ],
  "Homa Bay": [
    "Homa Bay Town",
    "Kabondo",
    "Karachwonyo",
    "Kasipul",
    "Mbita",
    "Ndhiwa",
    "Rangwe",
    "Suba",
  ],
  Migori: [
    "Awendo",
    "Kuria East",
    "Kuria West",
    "Mabera",
    "Ntimaru",
    "Rongo",
    "Suna East",
    "Suna West",
    "Uriri",
  ],
  Kisii: [
    "Bonchari",
    "Bomachoge Borabu",
    "Bomachoge Chache",
    "Kitutu Chache North",
    "Kitutu Chache South",
    "Nyaribari Chache",
    "Nyaribari Masaba",
    "South Mugirango",
  ],
  Nyamira: [
    "Borabu",
    "Manga",
    "Masaba North",
    "Nyamira North",
    "Nyamira South",
  ],
  Nairobi: [
    "Dagoretti North",
    "Dagoretti South",
    "Embakasi Central",
    "Embakasi East",
    "Embakasi North",
    "Embakasi South",
    "Embakasi West",
    "Kamukunji",
    "Kasarani",
    "Kibra",
    "Lang'ata",
    "Makadara",
    "Mathare",
    "Roysambu",
    "Ruaraka",
    "Starehe",
    "Westlands",
  ],
};

// Function to populate sub-county dropdown based on selected county
function populateSubCounties(countySelectId, subCountySelectId) {
  const countySelect = document.getElementById(countySelectId);
  const subCountySelect = document.getElementById(subCountySelectId);

  if (!countySelect || !subCountySelect) {
    console.error("County or Sub-County select elements not found");
    return;
  }

  // Clear existing sub-county options
  subCountySelect.innerHTML = '<option value="">All Sub-Counties</option>';

  const selectedCounty = countySelect.value;

  if (selectedCounty && kenyaLocations[selectedCounty]) {
    // Add sub-counties for the selected county
    kenyaLocations[selectedCounty].forEach((subCounty) => {
      const option = document.createElement("option");
      option.value = subCounty;
      option.textContent = subCounty;
      subCountySelect.appendChild(option);
    });
  }
}

// Function to get all counties as an array
function getAllCounties() {
  return Object.keys(kenyaLocations).sort();
}

// Function to get sub-counties for a specific county
function getSubCountiesForCounty(county) {
  return kenyaLocations[county] || [];
}

// Helper functions for searchable dropdown functionality
function searchCounties(query) {
  if (!query) return getAllCounties();
  const lowerQuery = query.toLowerCase();
  return getAllCounties().filter((county) =>
    county.toLowerCase().includes(lowerQuery)
  );
}

function searchSubCounties(county, query) {
  if (!county) return [];
  const subCounties = getSubCountiesForCounty(county);
  if (!query) return subCounties;
  const lowerQuery = query.toLowerCase();
  return subCounties.filter((subCounty) =>
    subCounty.toLowerCase().includes(lowerQuery)
  );
}

// Expose functions and data to global scope for Alpine.js
window.getAllCounties = getAllCounties;
window.getSubCountiesForCounty = getSubCountiesForCounty;
window.searchCounties = searchCounties;
window.searchSubCounties = searchSubCounties;
window.kenyaLocations = kenyaLocations;

// Function to initialize county dropdown
function initializeCountyDropdown(countySelectId, subCountySelectId) {
  const countySelect = document.getElementById(countySelectId);

  if (!countySelect) {
    console.error("County select element not found");
    return;
  }

  // Clear existing options except "All Counties"
  countySelect.innerHTML = '<option value="">All Counties</option>';

  // Add all counties
  getAllCounties().forEach((county) => {
    const option = document.createElement("option");
    option.value = county;
    option.textContent = county;
    countySelect.appendChild(option);
  });

  // Add event listener to county select
  countySelect.addEventListener("change", () => {
    populateSubCounties(countySelectId, subCountySelectId);
  });
}
