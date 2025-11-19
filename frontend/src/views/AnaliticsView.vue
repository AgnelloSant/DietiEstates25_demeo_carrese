<template>
  <div class="analytics-dashboard">
    <!-- KPI CARDS -->
    <div class="stats-grid">
      <div class="stat-card">Visite prenotate: {{ stats.bookings }}</div>
      <div class="stat-card">Offerte ricevute: {{ stats.bids }}</div>
    </div>


    <div class="wide-content-container"> 
      <BidChart v-if="trendChartData.labels.length > 0" :chart-data="trendChartData" />
        <div v-else>caricamento dati..</div>
    </div>

    <!-- TABELLA BID SUMMARY -->
    <table class="bids-table">
      <thead>
        <tr>
          <th>Annuncio</th>
          <th>Offerte</th>
          <th>Prezzo medio</th>
          <th>Ultima offerta</th>
          <th>Azioni</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in bidsSummary" :key="row.propertyId">
          <td>{{ row.propertyTitle }}</td>
          <td>{{ row.count }}</td>
          <td>{{ row.avgPrice }}</td>
          <td>
            {{formatPublishedAt( row.lastDate )}}
          </td>
          <td>
            <button @click="showBidDetails(row.propertyId)">Dettagli</button>
          </td>
        </tr>
      </tbody>
    </table>

  <div v-if="isDialogVisible" class="modal-overlay" @click.self="closeDetails">
    <div class="modal modal--medium">
        <h3>Dettagli offerte</h3>

        <div class="bid-header">
            <strong class="text-info">Importo</strong>
            <strong class="text-info">Data</strong>
            <strong class="text-info">Azioni</strong>
        </div>

        <div v-if="selectedBids && selectedBids.length > 0" class="bid-list">
            <div v-for="b in selectedBids" :key="b.id" class="bid-row">
                <div class="white-text">€ {{ b.amount.toFixed(2) }}</div> 
                
                <div class="white-text">{{ formatPublishedAt(b.publishedAt) }}</div>
                
                <div class="bid-actions">
                    <button @click="acceptBid(b.id)" class="btn-accept">Accetta</button>
                    <button @click="rejectBid(b.id)" class="btn-bulk-delete"> Rifiuta </button>
                </div>
            </div>
        </div>
        <div v-else class="empty-state">
            Nessuna offerta trovata per questa proprietà.
        </div>

        <button @click="closeDetails" class="btn btn-close">Chiudi</button>
    </div>
</div>
</div>



</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { usePropertyStore } from "@/stores/properties"
import BidChart from "@/components/BidChart.vue"
import { BidDailyCount } from "@/types/Properties"

const propertyStore = usePropertyStore()

const bidsSummary = ref<any[]>([])
const stats = ref({
  bookings: 0,
  bids: 0
})
const selectedBids = ref<any[] | null>(null)
const isDialogVisible = ref(false)

const trendChartData = ref({
    labels: [], 
    datasets: [{
        label: 'Offerte giornaliere',
        data: [],
        backgroundColor: '#42b983', 
        borderColor: '#42b983',
    }],
});

const formatTrendDataForChartJS = (trendData: BidDailyCount[]) => {
    trendChartData.value = {
        labels: trendData.map(d => d.dateLabel),
        datasets: [{
            label: 'Conteggio Offerte Ricevute',
            backgroundColor: 'rgba(54, 162, 235, 0.5)', 
            borderColor: '#36A2EB',
            data: trendData.map(d => d.offerCount),
            tension: 0.4,
            fill: true
        }],
    };
};

const loadChartData = async () => {
    console.log("Inizio caricamento dati trend...");
    
    const dailyCountData = await propertyStore.fetchTrend();

    if (dailyCountData && dailyCountData.length > 0) {
        formatTrendDataForChartJS(dailyCountData);
        console.log("Dati del trend caricati con successo.");
    } else {
        console.log("Nessun dato di trend ricevuto.");
    }
};

onMounted(async () => {
  const resSummary = await propertyStore.fetchBidsSummaryByUserOwned()

  if (resSummary) {
    bidsSummary.value = resSummary
    stats.value.bids = resSummary.reduce((acc: number, cur: any) => acc + cur.count, 0)
  }

  const resBookings = await propertyStore.fetchReservationsByUser()
  await loadChartData();

  if (resBookings) {
    stats.value.bookings = resBookings.length
  }
})

const showBidDetails = async (propertyId: number) => {
  const res = await propertyStore.fetchBidsByProperty(propertyId)
  if (res && res.length > 0) {
    selectedBids.value = res
    console.log(selectedBids.value)
    isDialogVisible.value = true
  }else{ 
    selectedBids.value = []
    isDialogVisible.value = false
  }
}

const formatPublishedAt = (dateTimeString: string) => {
    if (!dateTimeString) return '-';
    const [datePart, timePart] = dateTimeString.split('T');

    const formattedDate = datePart.split('-').reverse().join('/'); // Giorno/Mese/Anno
    const formattedTime = timePart.substring(0, 5);

    //return `${datePart} ${timePart.substring(0, 5)}`; 

    return `${formattedDate} ${formattedTime}`;
};

const acceptBid = async (bidId: number) => { 
  console.log(`Accetta Offerta ID: ${bidId}`)
  //TODO: logica per accettare un offerta 
}

const rejectBid = async (bidId: number) => { 
  console.log(`Rifiuta Offerta ID: ${bidId}`)
//TODO: Logica per rifiutare
}

const closeDetails = () => {
  isDialogVisible.value = false
}
</script>

<style scoped>
.analytics-dashboard {
  padding: 20px;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  margin: 20px 0;
}
.stat-card {
  background: #f3f3f3;
  padding: 15px;
  border-radius: 10px;
  text-align: center;
  font-weight: bold;
}
.bids-table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}
.bids-table th, .bids-table td {
  border: 1px solid #ddd;
  padding: 10px;
}

</style>
