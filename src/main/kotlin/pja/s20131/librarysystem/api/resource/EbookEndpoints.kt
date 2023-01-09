package pja.s20131.librarysystem.api.resource

import java.time.LocalDate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.resource.Content
import pja.s20131.librarysystem.domain.resource.Format
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Size
import pja.s20131.librarysystem.domain.resource.SizeUnit
import pja.s20131.librarysystem.domain.resource.Title
import kotlin.random.Random

@RestController
@RequestMapping("/ebooks")
class EbookEndpoints {

    @GetMapping
    fun getAllEbooks(): List<GetEbookResponse> =
        listOf(
            GetEbookResponse(
                ResourceBasicData(Title("Chrzest ognia"), ReleaseDate(LocalDate.of(1995, 4, 15)), null, Series("Wiedźmin"), ResourceStatus.WITHDRAWN),
                Format("epub"),
                Content(Random.Default.nextBytes(100)),
                Size(756.3),
                SizeUnit.kB
            ),
            GetEbookResponse(
                ResourceBasicData(Title("Krew elfów"), ReleaseDate(LocalDate.of(1993, 7, 20)), null, Series("Wiedźmin"), ResourceStatus.AVAILABLE),
                Format("pdf"),
                Content(Random.Default.nextBytes(100)),
                Size(1200.0),
                SizeUnit.kB
            ),
        )
}
