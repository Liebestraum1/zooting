<template>
  <div class="match__container">
    <div class="match__page">
      <div class="button"><p>MATCHING</p></div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style lang="scss" scoped>
.match__container {
  @apply flex flex-col items-center col-span-3 col-start-3 py-5;
}
.match__page {
  @apply flex flex-grow justify-center items-center;
}

.match__ready {
  @apply h-32 flex items-center;
}
.button {
	text-align: center;
	font-size: 42px;
  font-weight: 700;
	color: #5647ab;
	width: 350px;
	height: 100px;

	border-radius: 30px;
	text-shadow: 
		0px 5px hsl(55, 100%, 74%), 
		0px 10px 10px #0003;
	box-shadow: 
		0px 0px 0px 15px #7360DF,
		0px 10px 0px 15px #5647ab,
		0px 20px 20px 15px #0003;
	cursor: pointer;
	border-radius: 100px 30px 100px 30px;

}

.button p {
	margin-top: 20px;
	transform: rotate(-3deg);
}

.button:hover {
	animation: .8s morph ease infinite;
}

.button:hover p {
	animation: .8s rot ease infinite;
}

@keyframes morph {
	0% {
		border-radius: 100px 30px 100px 30px;
	} 50% {
		border-radius: 30px 100px 30px 100px;
	} 100% {
		border-radius: 100px 30px 100px 30px;
	}
}

@keyframes rot {
	0% {
		transform: rotate(-3deg);
	} 50% {
		transform: rotate(3deg);
	} 100% {
		transform: rotate(-3deg);
	}
}
</style>