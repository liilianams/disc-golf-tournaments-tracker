function resetFilters() {
  document.getElementById("location-filter").selectedIndex = 0;
  document.getElementById("month-filter").selectedIndex = 0;
  document.getElementById("tier-filter").selectedIndex = 0;
  filterTournaments();
}

function validateTier(rowTier, tier) {
  if (rowTier?.includes('XC') && tier === 'C') {
    return false;
  }

  return rowTier?.includes(tier);
}

function filterTournaments() {
  const location = document.getElementById('location-filter').value;
  const month = document.getElementById('month-filter').value;
  const tier = document.getElementById('tier-filter').value;
  const rows = document.querySelectorAll('.tournament-row');

  rows.forEach(row => {
    const rowLocation = row.querySelector('.row-location').textContent;
    const rowMonth = row.querySelector('.row-date').textContent.split(' ')[0];
    const rowTier = row.querySelector('.row-tier')?.textContent;

    if (
      (!location || rowLocation === location) &&
      (!month || rowMonth.includes(month)) &&
      (!tier || validateTier(rowTier, tier))
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
  window.open(url);
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
