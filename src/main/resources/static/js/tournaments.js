function resetFilters() {
  document.getElementById("country-filter").selectedIndex = 0;
  document.getElementById("month-filter").selectedIndex = 0;
  document.getElementById("tier-filter").selectedIndex = 0;
  document.getElementById("search-filter").value = "";
  filterTournaments();
}

function resetMyFilters() {
  document.getElementById("location-filter").selectedIndex = 0;
  document.getElementById("month-filter").selectedIndex = 0;
  document.getElementById("tier-filter").selectedIndex = 0;
  filterFavoriteTournaments();
}

function filterByTier(rowTier, filterTier) {
  // Handle rows that don't have a tier
  if (!rowTier) {
    return false;
  }

  // Normalize filter tier
  filterTier = filterTier.trim().concat('-tier');

  // Handle tiers formatted like: 'teams · xc-tier'
  rowTier = rowTier.replace(' · ', '/').trim();

  // Handle rows that contain composite tiers like 'b/c-tier' or 'teams/xc-tier'
  const rowTiers = rowTier.split('/').map(t => t.replace('-tier', '').concat('-tier'));

  return rowTiers.includes(filterTier);
}

function filterTournaments() {
  const searchTerm = document.getElementById('search-filter').value.toLowerCase();
  const countryFilter = document.getElementById('country-filter').value.toLowerCase();
  const monthFilter = document.getElementById('month-filter').value.slice(0, 3).toLowerCase();
  const tierFilter = document.getElementById('tier-filter').value.toLowerCase();

  const rows = document.querySelectorAll('.tournament-row');

  rows.forEach(row => {
    const rowName = row.querySelector('.row-tournament-name').textContent.toLowerCase();
    const rowCourse = row.querySelector('.row-course').textContent.toLowerCase();
    const rowLocation = row.querySelector('.row-location-details').textContent.toLowerCase();
    const rowMonth = row.querySelector('.row-date').textContent.split(' ')[0].toLowerCase();
    const rowTier = row.querySelector('.row-tier')?.textContent.toLowerCase();

    const matchesSearchTerm = !searchTerm || rowName.includes(searchTerm) ||
                              rowCourse.includes(searchTerm) || rowLocation.includes(searchTerm);
    const matchesState = !countryFilter || rowLocation.includes(countryFilter);
    const matchesMonth = !monthFilter || rowMonth === monthFilter;
    const matchesTier = !tierFilter || filterByTier(rowTier, tierFilter);

    if (matchesSearchTerm && matchesState && matchesMonth && matchesTier) {
      row.style.display = '';
    } else {
      row.style.display = 'none';
    }
  });
}

function filterFavoriteTournaments() {
  const location = document.getElementById('location-filter').value.toLowerCase();
  const month = document.getElementById('month-filter').value.toLowerCase();
  const tier = document.getElementById('tier-filter').value.toLowerCase();
  const rows = document.querySelectorAll('.tournament-row');

  rows.forEach(row => {
    const rowLocation = row.querySelector('.row-location').textContent.toLowerCase();
    const rowMonth = row.querySelector('.row-date').textContent.split(' ')[0].toLowerCase();
    const rowTier = row.querySelector('.row-tier')?.textContent.toLowerCase();

    if (
      (!location || rowLocation === location) &&
      (!month || rowMonth.includes(month)) &&
      (!tier || filterByTier(rowTier, tier))
    ) {
      row.style.display = '';
    } else {
      row.style.display = 'none';
    }
  });
}

function safeTextContent(element, selector) {
  return (element.querySelector(selector)?.textContent || "").toLowerCase();
}

function openGoogleMaps(event) {
  console.log(event)
  const courseText = event.textContent.split('→')[0].trim();
  console.log(courseText)
  const courseName = encodeURIComponent(addDiscGolfCourse(courseText));
  console.log(courseName)
  const location = encodeURIComponent(event.parentNode.querySelector('.row-location-details').textContent);
  const query = `${courseName}, ${location}`;
  const url = `https://www.google.com/maps/search/?api=1&query=${query}`;
  window.open(url, '_blank');
  console.log(url)
}

function addDiscGolfCourse(courseName) {
  const hasDiscGolfCourse = courseName.toLowerCase().includes('golf') ||
    courseName.toLowerCase().includes('course') ||
    courseName.toLowerCase().includes('park') ||
    courseName.toLowerCase().includes('disc');
  if (!hasDiscGolfCourse) {
    courseName += ' Disc Golf Course';
  }
  return courseName;
}
