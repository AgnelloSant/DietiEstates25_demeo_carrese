<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal modal--small">
      <h3 class="modal-title">{{ title }}</h3>
      <p class="modal-message">{{ message }}</p>

      <div class="modal-actions">
        <button class="btn-secondary" @click="$emit('cancel')">No</button>
        <button class="btn-primary" @click="$emit('confirm')">Sì</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  visible: boolean
  title: string
  message: string
}>()

defineEmits<{
  (e: "confirm"): void
  (e: "cancel"): void
}>()
</script>

<style scoped>

/* .modal {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  animation: fadeIn 0.2s ease-in-out;
}

.modal-title {
  font-size: 1.3rem;
  margin-bottom: 0.5rem;
}

.modal-message {
  font-size: 1rem;
  margin-bottom: 1rem;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
} */

@keyframes fadeIn {
  from { transform: translateY(-10px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style>
