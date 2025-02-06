import {Router} from "vue-router";
import NProgress from 'nprogress';

export default function setupUserLoginInfoGuard(router: Router) {
    router.beforeEach(async (to, from, next) => {
        console.log('from==>', from)
        console.log('to==>', to)
        // NProgress.start();
        const token = localStorage.getItem('satoken');
        if (token) {
            next()
            // NProgress.done()
        } else {
            if (to.path === '/login') {
                next()
                // NProgress.done()
            }
            next({path: '/login'});
            // NProgress.done()
        }

    })


}