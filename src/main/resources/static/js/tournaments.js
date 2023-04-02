function resetFilters() {
  document.getElementById("course-filter").selectedIndex = 0;
  document.getElementById("city-filter").selectedIndex = 0;
  document.getElementById("state-filter").selectedIndex = 0;
  document.getElementById("month-filter").selectedIndex = 0;
  document.getElementById("tier-filter").selectedIndex = 0;
  filterTournaments();
}

function resetMyFilters() {
  document.getElementById("location-filter").selectedIndex = 0;
  document.getElementById("month-filter").selectedIndex = 0;
  document.getElementById("tier-filter").selectedIndex = 0;
  filterMyTournaments();
}

function validateTier(rowTier, tier) {
  if (rowTier?.includes('XC') && tier === 'C') {
    return false;
  }

  return rowTier?.includes(tier);
}

function filterTournaments() {
  const course = document.getElementById('course-filter').value;
  const city = document.getElementById('city-filter').value;
  const state = document.getElementById('state-filter').value;
  const month = document.getElementById('month-filter').value;
  const tier = document.getElementById('tier-filter').value;
  const rows = document.querySelectorAll('.tournament-row');

  rows.forEach(row => {
    const rowCourse = row.querySelector('.row-course').textContent;
    const rowCity = row.querySelector('.row-city').textContent;
    const rowState = row.querySelector('.row-state').textContent;
    const rowMonth = row.querySelector('.row-date').textContent.split(' ')[0];
    const rowTier = row.querySelector('.row-tier')?.textContent;

    if (
      (!month || rowMonth.includes(month)) &&
      (!tier || validateTier(rowTier, tier)) &&
      (!city || rowCity.includes(city)) &&
      (!state || rowState.includes(state)) &&
      (!course || rowCourse.includes(course))
    ) {
      row.style.display = '';
    } else {
      row.style.display = 'none';
    }
  });
}

function filterMyTournaments() {
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
