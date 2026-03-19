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

@keyframes fadeIn {
  from { transform: translateY(-10px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style>
