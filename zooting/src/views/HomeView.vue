<template>
  <div class="home__container">
    <TheSideBar
      :dm-res="dmRes"
      @current-dm-room-id="currentDmRoomId"
    />
    <Social
      class="ms-14"
    />
    <Ready
      :matching-count="matchingCount"
    />
  </div>
</template>

<script setup lang="ts">
import {onBeforeUnmount, onMounted, ref, watch} from 'vue'
import {useAccessTokenStore, useStore} from "@/stores/store"
import TheSideBar from '@/components/TheSideBar.vue'
import Social from '@/components/home/Social.vue'
import Ready from '@/components/home/Ready.vue'
import DmSound from '/assets/sounds/dm.mp3'

const {VITE_SERVER_API_URL} = import.meta.env

const store = useAccessTokenStore()
const persistStore = useStore()

const userInfo = ref(store.userInfo)

const emit = defineEmits(['receiveMessage', 'getOpenviduToken', 'getMatchingCompleteTime'])
const dmRes = ref<any>(null)  // dm 메시지 객체
const dmRoomId = ref<number>(0) // dm 방 id
const dmSound = new Audio(DmSound)  // dm 알림 소리

// @ts-ignore
const socket = new SockJS(`${VITE_SERVER_API_URL}/ws`)
// @ts-ignore
const stompClient = Stomp.over(socket)
stompClient.debug = null
let intervalId: any;
const START_HEART_CHECK = 5 * 1000;
const HEART_CHECK_INTERVAL = 15 * 1000; // heartbeat check interval time
const intervalTime = ref<number>(START_HEART_CHECK);

const matchingCount = ref<number>(0)  // 매칭 대기 인원 수

function playSound(sound: any) {
  sound.currentTime = 0
  sound.play()
}

const currentDmRoomId = (id: number) => {
  dmRoomId.value = id
}

watch(() => store.userInfo, (update) => {
  userInfo.value = update
})

onMounted(async () => {
  const result = await store.checkCompletedSignUp()
  if (result === 'USER') {
    await store.getUserInfo()
  }
  startHeartbeat();

  // 매칭 중에 새로고침했을 시 매칭 취소
  if (localStorage.getItem("sessionRoomId")) {
    store.meetingExit()
  }
})

const startHeartbeat = () => {
  intervalId = setInterval(() => {
    if (userInfo.value && userInfo.value.email) {
      stompClient.send('/api/pub/member/heartbeat', {}, JSON.stringify({
        memberId: userInfo.value?.email,
        nickname: userInfo.value?.nickname,
      }));
    }
  }, intervalTime.value);
}

// 소켓 통신 연결 요청
stompClient.connect(
    {},
    () => {
      console.log('Connected to WebSocket')
      onConnected()
    },
    () => {
      console.log("Could not WebSocket server")
    }
)

// 연결 해제 시 호출
socket.onclose = () => {
  console.log('Disconnected from WebSocket')
}

const onConnected = () => {
  stompClient.subscribe(`/api/sub/${userInfo.value?.email}`, (message: any) => {
    const type = JSON.parse(message.body).type;
    const time = JSON.parse(message.body).time;
    const res = JSON.parse(message.body).result;

    // MESSAGE
    if (type === 'MESSAGE') {
      // 현재 open 된 dmRooId인 경우 메시지 전송
      if (dmRoomId.value === res.dmRoomId) {
        dmRes.value = {
          ...res,
          createdAt: new Date(time).toLocaleTimeString('ko-KR', {timeStyle: 'short', hour12: false})
        };
      } else {
        // 새로운 메시지 알림
        persistStore.newMessage.push(res.sender);
        playSound(dmSound);
      }
    }
    // 매칭 완료
    else if (type === 'MATCH') {
      store.MatchingComplete();
    }
    // 미팅 시작 (다대다 / 일대일)
    else if (type === 'OPENVIDU') {
      store.pushMeetingRoom(res, Date.parse(time), type);
    } else if (type === 'ONETOONE') {
      store.pushMeetingRoom(res, Date.parse(time), type);
    }
    // 유저 상태 정보
    else if (type == 'HEARTBEAT') {
      if (intervalTime.value === START_HEART_CHECK) {
        clearInterval(intervalId);
        intervalTime.value = HEART_CHECK_INTERVAL;
        startHeartbeat();
      }
      const onlineFriends = res.onlineFriends;
      for (const friend of store.friendList) {
        friend.isOnline = onlineFriends.includes(friend.email);
      }
    }
    // 일대일 미팅 요청 수신
    else if (type === 'MEETING') {
      store.meetingSender = res.nickname;
      store.isRecieveMeeting = true;
      console.log(time);
    }
    // 미팅 거절
    else if (type === 'REJECT') {
      store.isRequesting = false;
      store.isMeetingReject = true;
    }
  });

  // 매칭 중인 인원
  stompClient.subscribe(`/api/sub/matching-count`, (matchingCountMessage: any) => {
    matchingCount.value = JSON.parse(matchingCountMessage.body);
  });
};
</script>

<style scoped>
.home__container {
  @apply h-screen flex flex-row w-full;
}
</style>
