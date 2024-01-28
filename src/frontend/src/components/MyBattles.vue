<template>
  <div class="container">

    <h2>My Battles</h2>

    <ul class="list-group" style="margin-top: 20%">
      <li
          class="list-group-item"
          v-for="(b, index) in myB"
          :key="index"
          style="display: flex; justify-content: space-between"
      >
        <a
            @click.prevent=""
        >{{b._id}}</a>
        <span class="visibility" style="color: dimgray">{{b.visibility}}</span>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'MyBattles',
  data() {
    return {
      myB: [
        {
          _id: 'Battle12',
          visibility: 'Tournament2'
        },
        {
          _id: 'Battle3',
          visibility: 'Tournament2'
        },
        {
          _id: 'Battle90',
          visibility: 'Tournament9  '
        }
      ]
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