<template>
  <div class="analytics-dashboard">
    <!-- KPI CARDS -->
    <div class="stats-grid">
      <div class="stat-card">Visite prenotate: {{ stats.bookings }}</div>
      <div class="stat-card">Offerte ricevute: {{ stats.bids }}</div>
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
          <td>{{ row.lastDate }}</td>
          <td>
            <button @click="showBidDetails(row.propertyId)">Dettagli</button>
          </td>
        </tr>
      </tbody>
    </table>


    <!-- MODALE DETTAGLI OFFERTE -->
    <div v-if="selectedBids" class="modal-overlay" @click.self="closeDetails">
      <div class="modal modal--medium">
        <h3>Dettagli offerte</h3>
        <ul>
          <li v-for="b in selectedBids" :key="b.id">
            {{ b.publishedAt }} — {{ b.amount }}
          </li>
        </ul>
        <button @click="closeDetails">Chiudi</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { usePropertyStore } from "@/stores/properties"

const propertyStore = usePropertyStore()

// Stato locale della dashboard
const bidsSummary = ref<any[]>([])
const stats = ref({
  bookings: 0,
  bids: 0
})
const selectedBids = ref<any[] | null>(null)


onMounted(async () => {
  // 🔹 Riepilogo offerte
  const resSummary = await propertyStore.fetchBidsSummaryByUserOwned()
 
  if (resSummary) {
    bidsSummary.value = resSummary
    stats.value.bids = resSummary.reduce((acc: number, cur: any) => acc + cur.count, 0)
  }

  // 🔹 Prenotazioni totali dell'owner
  const resBookings = await propertyStore.fetchReservationsByUser()
  if (resBookings) {
    stats.value.bookings = resBookings.length
  }
})

const showBidDetails = async (propertyId: number) => {
  const res = await propertyStore.fetchBidsByProperty(propertyId)
  if (res?.data) {
    selectedBids.value = res.data
  }
}

const closeDetails = () => {
  selectedBids.value = null
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
/* .modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 400px;
} */
</style>
