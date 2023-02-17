package ly.android.material.code.tool.data.entity

data class HomePageBean(
    var position: Int = 0,
    var state: State = State.POSITION
)

enum class State {
    POSITION, FLOW
}
