<template>
  <div class="container">

    <h2>My Tournaments</h2>

    <ul class="list-group" style="margin-top: 20%">
      <li
          class="list-group-item"
          v-for="(t, index) in myT"
          :key="index"
          style="display: flex; justify-content: space-between"
      >
        <a>{{t._id}}</a>
        <span class="visibility" style="color: dimgray">{{t.visibility}}</span>
      </li>
    </ul>


  </div>
</template>

<script>
export default {
  name: 'MyTournaments',
  data() {
    return {
      myT: [],
      requestOptions: {
        method: "GET",
        headers: {}
      }
    }
  }
}
</script>

<style>
h2 {
  margin-top: 5%;
  margin-left: 5%;
}
</style>