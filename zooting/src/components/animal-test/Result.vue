<template>
  <div class="h-screen">
    <div class="grid grid-cols-1 md:gap-32 md:grid-cols-2">
      <div class="flex flex-col justify-center mt-10 ml-5 md:mt-0">
        <p class="text-4xl font-bold">{{ nickname }}님은</p>
        <p class="mt-3 text-4xl font-bold md:mt-5"><span style="color: #F2AFEF;">{{ resultAnimal }}</span>상이네요</p>

        <div>
          <button type="button" v-if="!isChartShows" @click.prevent="showCharts" class="mt-8 rounded-full bg-white px-4 py-2.5 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50">결과 자세히 보기</button>
          <div id="chart"></div>
        </div>
      </div>

      <div class="flex flex-col flex-wrap items-center justify-center md:h-screen">
        <img src="/assets/images/animal/dog.png" alt="강아지 사진" style="height: 50vh;"
        v-if="resultAnimal === '강아지'">
        <img src="/assets/images/animal/cat.png" alt="고양이 사진" style="height: 50vh;"
        v-if="resultAnimal === '고양이'">
        <img src="/assets/images/animal/bear.png" alt="곰 사진" style="height: 50vh;"
        v-if="resultAnimal === '곰'">
        <img src="/assets/images/animal/dino.png" alt="공룡 사진" style="height: 50vh;"
        v-if="resultAnimal === '공룡'">
        <img src="/assets/images/animal/rabbit.png" alt="토끼 사진" style="height: 50vh;"
        v-if="resultAnimal === '토끼'">
        <img src="/assets/images/animal/penguin.png" alt="펭귄 사진" style="height: 50vh;"
        v-if="resultAnimal === '펭귄'">
        <img src="/assets/images/animal/deer.png" alt="사슴 사진" style="height: 50vh;"
        v-if="resultAnimal === '사슴'">

        <div class="flex flex-col items-center justify-center md:mt-10">
          <svg fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 animate-bounce">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
          </svg>
          <button type="button" @click.prevent="goPersonalityTest" class="md:mt-3 rounded-md px-3.5 py-2.5 text-sm font-semibold text-white shadow-sm bg-gradient-to-br from-purple-600 to-blue-500 hover:bg-gradient-to-bl">다음으로</button>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Popover, PopoverButton, PopoverPanel } from '@headlessui/vue'
import ApexCharts from 'apexcharts'

const nickname = ref('회원')

const props = defineProps({
  resultAnimal: String,
  allAnimal: Object,
})

const router = useRouter()

const goPersonalityTest = function() {
  router.push({ name: 'personality-test' })
}

// ApexCharts 설정
const options = {
  series: [
        {
          name: "분석 결과 (%)",
          color: "#C499F3",
          data: Object.values(props.allAnimal).slice(1),
        },
      ],
      chart: {
        sparkline: {
          enabled: false,
        },
        type: "bar",
        width: "100%",
        height: 300,
        toolbar: {
          show: false,
        }
      },
      fill: {
        opacity: 1,
      },
      plotOptions: {
        bar: {
          horizontal: true,
          columnWidth: "100%",
          borderRadiusApplication: "end",
          borderRadius: 5,
          dataLabels: {
            position: "top",
          },
        },
      },
      dataLabels: {
        enabled: false,
      },
      xaxis: {
        labels: {
          show: false,
          style: {
            fontFamily: "Inter, sans-serif",
            cssClass: 'text-xs font-normal fill-gray-500 dark:fill-gray-400'
          },
          formatter: function(value) {
            return value + "%"
          }
        },
        categories: props.allAnimal.gender === "male" ? ["강아지", "고양이", "곰", "공룡", "토끼"] : ["강아지", "고양이", "펭귄", "사슴", "토끼"],
        axisTicks: {
          show: false,
        },
        axisBorder: {
          show: false,
        },
      },
      yaxis: {
        labels: {
          show: true,
          style: {
            fontFamily: "Inter, sans-serif",
            cssClass: 'text-xs font-normal fill-gray-500 dark:fill-gray-400'
          }
        }
      },
      grid: {
        show: true,
        strokeDashArray: 4,
        padding: {
          left: 2,
          right: 2,
          top: -20
        },
      }
}

let chart = null;

const isChartShows = ref(false)

const showCharts = (() => {
  isChartShows.value = true
  chart = new ApexCharts(document.querySelector("#chart"), options);
  chart.render();
})

</script>

<style scoped>

#chart {
  max-width: 650px;
  margin: 35px auto;
}
</style>