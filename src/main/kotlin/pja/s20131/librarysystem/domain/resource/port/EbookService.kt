package pja.s20131.librarysystem.domain.resource.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EbookService(
    val ebookRepository: EbookRepository
) {

    fun getAllEbooks() = ebookRepository.getAll()
}
