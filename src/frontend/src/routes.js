import {createRouter, createWebHistory} from "vue-router";
import Login from "@/views/Login.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/login',
            name: 'LoginSignup',
            component: Login
        }
    ]
})

export default router