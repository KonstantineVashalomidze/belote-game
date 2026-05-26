const otaxisId = localStorage.getItem('otaxisId');
const zedmetsaxeli = localStorage.getItem('zedmetsaxeli');

const erorisSpani = document.querySelector('#erori #value');
erorisSpani.textContent = '';

if (!otaxisId || !zedmetsaxeli) {
    erorisSpani.textContent = 'მონაცემები არ მოიძებნა';
}

let shemowmebisIntervali = null;

async function sheamowmeMdgomareoba() {
    try {
        const response = await fetch(`/api/tamashi/${otaxisId}/mdgomareoba?zedmetsaxeli=${encodeURIComponent(zedmetsaxeli)}`);
        if (!response.ok) {
            const eroriTexti = await response.text();
            erorisSpani.textContent = eroriTexti;
            return;
        }

        const monacemebi = await response.json();
        ganaaxleEkrani(monacemebi);

        const motamasheebi = monacemebi.motamasheebi || [];
        if (motamasheebi.length === 4) {
            if (shemowmebisIntervali) clearInterval(shemowmebisIntervali);
            window.location.href = '/tamashi.html';
        }

    } catch (err) {
        erorisSpani.textContent = 'ქსელის შეცდომა: ' + err.message;
    }
}

function ganaaxleEkrani(monacemebi) {
    const motamasheebi = monacemebi.motamasheebi || [];
    const gundiAShiVincaa = document.querySelector('#a-gundhi-vincaa-magati-sia');
    const gundiBShiVincaa = document.querySelector('#b-gundhi-vincaa-magati-sia');
    const otaxisIdisSpani = document.querySelector('#otaxisId');
    gundiAShiVincaa.textContent = '';
    gundiBShiVincaa.textContent = '';
    otaxisIdisSpani.textContent = '';

    let motamasheRomelGundsacEkutvnis = null;
    motamasheebi.forEach(motamashe => {
        const li = document.createElement('li');
        li.textContent = motamashe.zedmetsaxeli;
        if (motamashe.gundi === 'A') {
            gundiAShiVincaa.appendChild(li);
            if (motamashe.zedmetsaxeli === zedmetsaxeli) motamasheRomelGundsacEkutvnis = 'A';
        } else if (motamshe.gundi === 'B') {
            gundiBShiVincaa.appendChild(li);
            if (motamashe.zedmetsaxeli === zedmetsaxeli) motamasheRomelGundsacEkutvnis = 'B';
        }
    });
    otaxisIdisSpani.textContent = monacemebi.otaxisId;
}


sheamowmeMdgomareoba();
shemowmebisIntervali = setInterval(sheamowmeMdgomareoba, 2000);

