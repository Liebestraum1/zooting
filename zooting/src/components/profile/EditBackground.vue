<template>
  <SuccessNotification
      :title="notify.title"
      :message="notify.message"
      :show-from-parent="showSuccess"
      @set-parent-show="setShowSuccess"
  />
  <FailNotification
      :title="notify.title"
      :message="notify.message"
      :show-from-parent="showFail"
      @set-parent-show="setShowFail"
  />
  <TransitionRoot as="template" :show="$props.showFromParent">
    <Dialog as="div" class="relative z-10" @close="closeDialog">
      <TransitionChild as="template" enter="ease-out duration-300" enter-from="opacity-0" enter-to="opacity-100"
                       leave="ease-in duration-200" leave-from="opacity-100" leave-to="opacity-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"/>
      </TransitionChild>

      <div class="fixed inset-0 z-10 w-screen overflow-y-auto">
        <div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
          <TransitionChild as="template" enter="ease-out duration-300"
                           enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
                           enter-to="opacity-100 translate-y-0 sm:scale-100" leave="ease-in duration-200"
                           leave-from="opacity-100 translate-y-0 sm:scale-100"
                           leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95">
            <DialogPanel
                class="relative transform overflow-hidden rounded-lg bg-white px-6 pb-8 pt-5 text-left shadow-xl transition-all max-h-fit min-w-72">
              <div class="absolute top-3 right-3">
        <span
            class="inline-flex items-center rounded-full bg-green-200/60 px-2 py-1 text-xs font-medium text-gray-600 w-auto h-8"
        >
          <div class="flex items-center justify-center tracking-tighter">
            <p class="px-2 py-1 lg:text-lg font-semibold text-center font-sans text-gray-900">
              {{ userInfo?.point ? userInfo.point : 0 }}
            </p>
            <p class="text-lg pr-2 py-2 font-medium text-center font-sans">Point</p>
          </div>
        </span>
              </div>
              <div class="pb-5 flex flex-row justify-center">
                <p class="text-center font-bold text-gray-900 lg:text-xl">배경 이미지</p>
              </div>
              <div>
                <!-- Content -->
                <ul role="list"
                    class="grid grid-cols-1 gap-x-10 gap-y-8 sm:gap-x-6 sm:grid-cols-1 lg:grid-cols-2 md:grid-cols-2">
                  <li v-for="image in images" :key="image.backgroundId"
                      class="relative lg:min-w-96 lg:max-w-96 md:min-w-60 sm:min-w-44">
                    <BackgroundItem
                        v-if="image"
                        :key="image.backgroundId"
                        :image="image"
                        :user-info="userInfo"
                        @set-show-success="setShowSuccess"
                        @set-show-fail="setShowFail"
                        @set-notify="setNotify"
                        @load-my-info="loadMyInfo"
                        @update-img-status="updateImgStatus"
                    />
                  </li>
                </ul>
              </div>
              <!-- 페이징 네비바 -->
              <div class="flex justify-center py-4">
                <nav class="flex items-center justify-between border-t border-gray-200 px-4 sm:px-0">
                  <div class="hidden md:-mt-px md:flex">
                    <div v-for="pageNumber in getPageRange()" :key="pageNumber">
                      <div :class="currentPage === pageNumber ? 'nav__item__selected': 'nav__item__not_selected'"
                           @click="loadBackgroundImage(pageNumber)">
                        {{ pageNumber + 1 }}
                      </div>
                    </div>
                  </div>
                </nav>
              </div>
              <!-- 확인 버튼 -->
              <div class="flex justify-center">
                <button type="button"
                        class="w-1/2 rounded-md bg-violet-500 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-violet-400 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-violet-500"
                        @click="closeDialog">돌아가기
                </button>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {Dialog, DialogPanel, TransitionChild, TransitionRoot} from '@headlessui/vue'
import SuccessNotification from "@/components/util/SuccessNotification.vue";
import FailNotification from "@/components/util/FailNotification.vue";
import type {Background, Notify, UserInfo} from "@/types/global";
import {loadBackgroundImageApi, loadMyBackgroundImageApi} from "@/api/background";
import BackgroundItem from "@/components/profile/BackgroundItem.vue";

const props = defineProps({
  userInfo: Object as () => UserInfo,
  showFromParent: {
    type: Boolean,
    default: false,
  },
})
const emits = defineEmits(["setParentShow", "loadMyInfo"]);

const closeDialog = () => {
  emits("setParentShow", false);
}

const showSuccess = ref(false);
const showFail = ref(false);
const notify = ref<Notify>({
  title: '',
  message: undefined,
});

const loadMyInfo = () => {
  emits("loadMyInfo");
}
const setShowSuccess = (status: boolean) => {
  showSuccess.value = status;
}
const setShowFail = (status: boolean) => {
  showFail.value = status;
}
const setNotify = (title: string, message: any) => {
  notify.value.title = title;
  notify.value.message = message;
}
const updateImgStatus = (status: boolean | undefined, isSelected: boolean | undefined, imageId: number) => {
  const image = images.value.find(background => background.backgroundId === imageId);
  if (!image) return;

  if (status) {
    image.status = status;
  }
  if (isSelected) {
    images.value.forEach(value => value.isSelected = false);
    image.isSelected = isSelected;
  }
}

const myImages = ref<Background[]>([]);
const images = ref<Background[]>([]);
const currentPage = ref<number>(0);
const totalPage = ref<number>(0);
const DEFAULT_SIZE = 4;
const DEFAULT_NAV_SIZE = 5;

const getPageRange = () => {
  let start = Math.max(currentPage.value - 3, 0);
  let end = totalPage.value - start + 1 > DEFAULT_NAV_SIZE ? start + DEFAULT_NAV_SIZE - 1 : totalPage.value;

  return Array.from({length: end - start + 1}, (_, i) => start + i);
}


// 배경 이미지 조회 (한번에 4개씩)
const loadBackgroundImage = async (page: number) => {
  await loadBackgroundImageApi({page: page, size: DEFAULT_SIZE},
      ({data}: any) => {
        if (data.status === 200 || data.status === 201) {
          const result = data["result"]["backgroundRes"]; // 이미지 리스트
          currentPage.value = data["result"]["currentPage"] ? data["result"]["currentPage"] : 0;
          totalPage.value = data["result"]["totalPage"] ? data["result"]["totalPage"] - 1 : 0;

          images.value = [];
          for (const r of result) {
            let img: Background = {
              backgroundId: r.backgroundId,
              fileName: r.fileName,
              imgUrl: r.imgUrl,
              price: r.price,
              status: false,
              isSelected: false,
            }

            const result = myImages.value.find(i => i.backgroundId === img.backgroundId);
            // 내가 소유한 배경의 경우
            if (result) {
              img.status = true;
            }
            // 현재 나의 배경인 경우
            if (img.backgroundId === props.userInfo?.backgroundId) {
              img.isSelected = true;
            }

            images.value.push(img);
          }
        } else {
          setNotify("배경 불러오기 실패", "배경 목록 조회에 실패하였습니다.");
          setShowFail(true);
        }
      },
      (error: any) => console.log(error)
  );
}
// 내 배경 이미지 조회
const loadMyBackgroundImage = async () => {
  await loadMyBackgroundImageApi(
      ({data}: any) => {
        if (data.status === 200 || data.status === 201) {
          const result = data["result"];

          myImages.value = [];
          for (const r of result) {
            myImages.value.push({
              backgroundId: r.backgroundId,
              fileName: r.fileName,
              imgUrl: r.imgUrl,
              price: r.price,
              status: true,
              isSelected: false,
            })
          }
        } else {
          setNotify("내 배경 불러오기 실패", "내 배경 목록 조회에 실패하였습니다.");
          setShowFail(true);
        }
      },
      (error: any) => console.log(error)
  );
}

onMounted(async () => {
  await loadMyBackgroundImage();
  await loadBackgroundImage(0);
});
</script>

<style scoped>

.nav__item__not_selected {
  @apply inline-flex items-center border-t-2 border-transparent px-4 pt-4 text-sm font-medium text-gray-500 hover:border-gray-300 hover:text-gray-700;
}

.nav__item__selected {
  @apply inline-flex items-center border-t-2 border-indigo-500 px-4 pt-4 text-sm font-medium text-indigo-600;
}

.group {
  position: relative;
  width: 100%;
  height: 0;
  padding-top: calc(100% / (20 / 8));
  overflow: hidden;
}

/* 이미지를 절대 위치로 설정하여 상위 div에 꽉 차게 함 */
.group img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover; /* 이미지 비율 유지 */
}
</style>