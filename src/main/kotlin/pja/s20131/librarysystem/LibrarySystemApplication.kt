package pja.s20131.librarysystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class LibrarySystemApplication

fun main(args: Array<String>) {
    runApplication<LibrarySystemApplication>(*args)
}
