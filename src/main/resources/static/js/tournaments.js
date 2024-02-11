function resetFilters() {
  document.getElementById("state-filter").selectedIndex = 0;
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
  const stateFilter = document.getElementById('state-filter').value.toLowerCase();
  const monthFilter = document.getElementById('month-filter').value.slice(0, 3).toLowerCase();
  const tierFilter = document.getElementById('tier-filter').value.toLowerCase();

  const rows = document.querySelectorAll('.tournament-row');

  rows.forEach(row => {
    const rowName = row.querySelector('.row-tournament-name').textContent.toLowerCase();
    const rowCourse = row.querySelector('.row-course').textContent.toLowerCase();
    const rowCity = row.querySelector('.row-city').textContent.toLowerCase();
    const rowState = row.querySelector('.row-state').textContent.toLowerCase();
    const rowMonth = row.querySelector('.row-date').textContent.split(' ')[0].toLowerCase();
    const rowTier = row.querySelector('.row-tier')?.textContent.toLowerCase();

    const matchesSearchTerm = !searchTerm || rowName.includes(searchTerm) ||
                              rowCourse.includes(searchTerm) || rowCity.includes(searchTerm);
    const matchesState = !stateFilter || rowState === stateFilter;
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

function openGoogleMaps(event) {
  const courseName = encodeURIComponent(addDiscGolfCourse(event.textContent));
  const city = encodeURIComponent(event.parentNode.querySelector('.row-city').textContent);
  const state = encodeURIComponent(event.parentNode.querySelector('.row-state').textContent);
  const location = `${courseName}, ${city}${state}`;
  const url = `https://www.google.com/maps/search/?api=1&query=${location}`;
  window.open(url, '_blank');
}

function addDiscGolfCourse(courseName) {
  const hasDiscGolfCourse = courseName.toLowerCase().includes('golf') ||
    courseName.toLowerCase().includes('course') ||
    courseName.toLowerCase().includes('park');
  if (!hasDiscGolfCourse) {
    courseName += ' Disc Golf Course';
  }
  return courseName;
}
