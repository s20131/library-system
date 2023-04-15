package pja.s20131.librarysystem.adapter.api.user

import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.user.port.AuthService

@RestController
@RequestMapping("/auth")
class AuthEndpoints(
    val authService: AuthService,
) {

    @PostMapping(consumes = ["application/json"])
    @RequestMapping("/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun register(@RequestBody request: RegisterUserRequest) {
        return authService.register(request.toCommand())
    }

    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    @RequestMapping("/login")
    fun login(@RequestBody request: AuthenticateUserRequest) {
        val credentials = request.toCredentials()
        val authentication = UsernamePasswordAuthenticationToken(credentials.username.value, credentials.password.value)
        authService.authenticate(authentication)
    }
}
