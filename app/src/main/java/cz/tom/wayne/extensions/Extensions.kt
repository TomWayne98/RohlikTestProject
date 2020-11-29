package cz.tom.wayne.extensions

inline fun <A, B, R> ifNotNull(a: A?, b: B?, doCrazyShit: (A, B) -> R) {
    if (a != null && b != null) {
        doCrazyShit(a, b)
    }
}