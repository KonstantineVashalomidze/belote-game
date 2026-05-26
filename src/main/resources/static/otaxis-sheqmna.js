const zedmetsaxelisVeli = document.querySelector('#zedmetsaxelis-veli');
const otaxisSheqmnisGhilaki = document.querySelector('#otaxis-sheqmnis-ghilaki');

otaxisSheqmnisGhilaki.addEventListener('click', async (e) => {
    e.preventDefault();

    const zedmetsaxeli = zedmetsaxelisVeli.value.trim();
    const erorisSpani = document.querySelector('#erori #value');
    erorisSpani.textContent = '';

    try {
        const response = await fetch('/api/tamashi/sheqmeni', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ zedmetsaxeli: zedmetsaxeli })
        });

        if (!response.ok) {
            erorisSpani.textContent = await response.text();
            return;
        }

        const monacemebi = await response.json();
        if (monacemebi.otaxisId) {
            localStorage.setItem('otaxisId', monacemebi.otaxisId);
            window.location.href = '/lodini.html';
        } else {
            erorisSpani.textContent = 'სერვერის პასუხი არ შეიცავს ოთახის ID-ს';
        }
    } catch (err) {
        erorisSpani.textContent = 'ქსელის შეცდომა, გთხოვთ სცადეთ თავიდან';
        console.error(err);
    }


});