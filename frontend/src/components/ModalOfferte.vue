<template>
  <div v-if="isVisible" class="modal-overlay" @click.self="close">
    <div class="modal modal--medium basic-card">
 
      <div class="modal-header">
        <span class="modal-title">Dettagli offerte</span>
        <button class="btn-secondary" style="padding: 0.4rem 0.8rem;" @click="close">✕</button>
      </div>
 
      <div v-if="bids && bids.length > 0" class="card-content">
        <div
          v-for="(b, index) in bids"
          :key="b.id"
          class="info-container"
          :style="{ borderBottom: index < bids.length - 1 ? '1px solid #eee' : 'none' }"
        >
          <div class="row-container" style="justify-content: space-between; gap: 1rem; padding: 0 0.5rem;">
            <div class="form-group" style="flex: 1;">
              <strong class="text-info">Importo</strong>
              <div class="bold-text">€ {{ b.amount.toFixed(2) }}</div>
            </div>
            <div class="form-group" style="flex: 1; align-items: flex-end;">
              <strong class="text-info">Data</strong>
              <div class="bold-text">{{ formatPublishedAt(b.publishedAt) }}</div>
            </div>
          </div>
        </div>
      </div>
 
      <div v-else class="empty-state">
        Nessuna offerta trovata per questa proprietà.
      </div>
 
      <div class="modal-actions">
        <button class="btn-secondary full-line-input" @click="close">Chiudi</button>
      </div>
 
    </div>
  </div>
</template>
 
<script setup lang="ts">
defineProps<{
  isVisible: boolean
  bids: any[] | null
}>()
 
const emit = defineEmits(['close'])
 
function close() {
  emit('close')
}
 
function formatPublishedAt(dateTimeString: string) {
  if (!dateTimeString) return '-'
  const [datePart] = dateTimeString.split('T')
  return datePart.split('-').reverse().join('/')
}
</script>