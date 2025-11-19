

<script setup lang="ts">
import { defineProps, ref, watch } from 'vue';
import { Line } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement,
  ChartData,
} from 'chart.js';

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement
);

const props = defineProps({
  chartData: {
    type: Object as () => ChartData<'line'>,
    required: true,
  },
});

//Opzioni del Grafico
const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
    },
    title: {
      display: true,
      text: 'Trend Offerte Ultimo Mese',
    },
  },
  scales: {
    y: {
      beginAtZero: false,
      title: {
        display: true,
        text: 'Numero di Offerte',
      },
    },
    x: {
      title: {
        display: true,
        text: 'Giorno del Mese',
      },
    },
  },
});
</script>

<template>
  <div class="chart-container">
    <Line :data="props.chartData" :options="chartOptions" />
  </div>
</template>

<style scoped>
.chart-container {
  height: 400px;
  width: 100%;
}
</style>