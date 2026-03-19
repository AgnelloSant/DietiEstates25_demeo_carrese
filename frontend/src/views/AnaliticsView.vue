<template>
  <div class="analytics-dashboard">
    <!-- KPI CARDS -->
    <div class="stats-grid">
      <div class="stat-card">Visite prenotate: {{ stats.bookings }}</div>
      <div class="stat-card">Offerte ricevute: {{ stats.bids }}</div>
    </div>

    <!-- EXCEL DOWNLOAD BUTTONS -->
    <div style="margin-bottom: 20px; text-align: center; display: flex; gap: 10px; justify-content: center;">
        <button style="padding: 10px 20px; background-color: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer;" @click="downloadExcel">Scarica Excel Prenotazioni</button>
        <button style="padding: 10px 20px; background-color: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer;" @click="downloadBidsExcelFile">Scarica Excel Offerte</button>
    </div>

    <div class="wide-content-container" style="margin: 0 auto;"> 
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
    <ModalOfferte
      :is-visible="isDialogVisible"
      :bids="selectedBids"
      @close="closeDetails"
    />
</div>



</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { usePropertyStore } from "@/stores/properties"
import BidChart from "@/components/BidChart.vue"
import { BidDailyCount } from "@/types/Properties"
import { downloadReservationsExcel, downloadBidsExcel } from "@/api/properties"
import ModalOfferte from "@/components/ModalOfferte.vue"

const propertyStore = usePropertyStore()

const bidsSummary = ref<any[]>([])
const stats = ref({
  bookings: 0,
  bids: 0
})
const selectedBids = ref<any[] | null>(null)
const isDialogVisible = ref(false)

const trendChartData = ref({
    labels: [] as string[], 
    datasets: [{
        label: 'Offerte giornaliere',
        data: [] as number[],
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

const downloadExcel = async () => {
  try {
    const response = await downloadReservationsExcel();
    
    // Create a Blob from the response data
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    
    // Extract filename from headers if possible, or default
    const contentDisposition = response.headers['content-disposition'];
    let fileName = 'prenotazioni.xlsx';
    if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename="?(.+)"?/);
        if (fileNameMatch && fileNameMatch.length === 2)
            fileName = fileNameMatch[1];
    }
    
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
  } catch (error) {
    console.error("Errore durante il download dell'Excel:", error);
    alert("Errore durante il download del file Excel.");
  }
}

const downloadBidsExcelFile = async () => {
  try {
    const response = await downloadBidsExcel();
    
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    
    const contentDisposition = response.headers['content-disposition'];
    let fileName = 'offerte.xlsx';
    if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename="?(.+)"?/);
        if (fileNameMatch && fileNameMatch.length === 2)
            fileName = fileNameMatch[1];
    }
    
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
  } catch (error) {
    console.error("Errore durante il download dell'Excel Offerte:", error);
    alert("Errore durante il download del file Excel Offerte.");
  }
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
