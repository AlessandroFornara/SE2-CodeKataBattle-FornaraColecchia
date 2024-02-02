<template>

  <div class="commands">
    <div class="topCommands">
      <button
          type="button"
          id="homeButton"
          class="btn"
          @click="backToHome()"
          style="font-size: 1.4rem; color: white"
      >
        <img src="@/assets/ICONlogo.svg" width="35" style="margin-top: 30%" alt="H">
      </button>

      <button
          type="button"
          id="TButton"
          class="btn"
          @click.prevent="myT()"
      >
        <img src="@/assets/trophy-01-svgrepo-com.svg" width="32" alt="T">
      </button>

      <button
          type="button"
          id="BButton"
          class="btn"
          @click.prevent="myB()"
      >
        <img src="@/assets/sword-bold-svgrepo-com.svg" width="32" alt="B">
      </button>
    </div>

    <div class="bottomCommands">
      <button
          type="button"
          id="aboutButton"
          class="btn"
          @click.prevent="gotoAbout()"
      >
        <img src="@/assets/question-mark-svgrepo-com.svg" width="32" alt="Q">
      </button>

      <button
          type="button"
          id="profileButton"
          class="btn"
          @click.prevent="gotoProfile()"
      >
        <img src="@/assets/profile-svgrepo-com.svg" width="32" alt="P">
      </button>

      <button
          type="button"
          id="logoutButton"
          class="btn"
          @click.prevent="logout()"
      >
        <img src="@/assets/logout-svgrepo-com.svg" width="32" alt="Q">
      </button>
    </div>

  </div>

  <div class="menu"
       v-if="menuSelector!==0"
  >
    <MyTournaments v-if="menuSelector===1"></MyTournaments>

    <MyBattles v-if="menuSelector===2"></MyBattles>

  </div>

  <router-view></router-view>




</template>

<script>
import MyTournaments from "@/components/MyTournaments.vue";
import MyBattles from "@/components/MyBattles.vue";
//import Profile from "@/components/Profile.vue";
export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: 'Dashboard',
  components: {MyBattles, MyTournaments},
  data() {
    return {
      menuSelector: 0,
    }
  },
  methods: {
    myT() {
      if (this.menuSelector===1){
        this.menuSelector = 0;
      } else {
        this.menuSelector = 1;
      }
    },
    myB() {
      if (this.menuSelector===2){
        this.menuSelector = 0;
      } else {
        this.menuSelector = 2;
      }
    },
    backToHome() {
      this.$router.push('/dashboard/home')
    },
    gotoAbout() {
      this.$router.push('/dashboard/home')
    },
    gotoProfile(){
      this.$router.push('/dashboard/profile')
    },
    logout() {
      localStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style>
.commands {
  height: 100vh;
  width: 60px;
  background-color: black;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}
#TButton {
  margin-top: 30%;
}
#BButton {
  margin-top: 10%;
}
#aboutButton {
  margin-top: 10%;
}
#profileButton {
  margin-top: 10%;
}
#logoutButton {
  margin-top: 10%;
  margin-bottom: 20%;
}
.menu {
  height: 100vh;
  min-width: 20vw;
  background-color: mediumseagreen;
  display: flex;
  flex-direction: column;
}


</style>