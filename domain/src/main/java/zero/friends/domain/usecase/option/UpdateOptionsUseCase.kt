package zero.friends.domain.usecase.option

import zero.friends.domain.model.Option
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject
import kotlin.reflect.KClass

class UpdateOptionsUseCase @Inject constructor(
    private val gamerRepository: GamerRepository
) {
    suspend operator fun invoke(
        gamerId: Long,
        options: List<Option>,
        targetClass: KClass<out Option>
    ) {
        if (options.isEmpty()) {
            gamerRepository.clearOption(gamerId, targetClass)
        } else {
            gamerRepository.updateOption(gamerId, options)
        }
    }
}