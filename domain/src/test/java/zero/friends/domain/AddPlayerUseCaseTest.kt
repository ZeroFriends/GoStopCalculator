package zero.friends.domain

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.usecase.player.AddAutoGeneratePlayerUseCase

class AddPlayerUseCaseTest {

    @Mock
    private lateinit var playerRepository: PlayerRepository

    @Mock
    private lateinit var gameRepository: GameRepository

    private lateinit var addAutoGeneratePlayerUseCase: AddAutoGeneratePlayerUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        addAutoGeneratePlayerUseCase = AddAutoGeneratePlayerUseCase(gameRepository, playerRepository)
    }

    @Test
    @DisplayName("빈_리스트_유저_추가")
    fun empty_add() {
        runBlocking {
            addAutoGeneratePlayerUseCase()
        }
    }
}