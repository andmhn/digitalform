import { HttpInterceptorFn } from "@angular/common/http";

export const authInterceptor: HttpInterceptorFn = (request, next) => {
    const email = localStorage.getItem('email');
    const password = localStorage.getItem('password');
    
    if(email && password){
        request = request.clone({
            setHeaders: {
                Authorization: 'Basic ' + btoa(email + ':' + password)
            }
        })
    }
    return next(request);
}