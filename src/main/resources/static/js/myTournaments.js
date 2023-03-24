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
