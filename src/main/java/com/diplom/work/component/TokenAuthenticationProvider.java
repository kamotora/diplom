//package com.diplom.work.component;
//
//import com.diplom.work.svc.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
//
//    @Autowired
//    UserService customerService;
//
//    @Override
//    protected void additionalAuthenticationChecks(UserDetails user, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
//            throws AuthenticationException {
//        //
//    }
//
//    @Override
//    protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
//
//        Object token = usernamePasswordAuthenticationToken.getCredentials();
//        return Optional
//                .ofNullable(token)
//                .map(String::valueOf)
//                .flatMap(customerService::findByToken)
//                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with authentication token=" + token));
//    }
//}
