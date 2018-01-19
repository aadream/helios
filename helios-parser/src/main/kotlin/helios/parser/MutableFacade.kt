package helios.parser

interface MutableFacade<J : Any> : Facade<J> {
    fun jarray(vs: ArrayList<J>): J
    fun jobject(vs: MutableMap<String, J>): J

    override fun singleContext() = object : FContext<J> {
        lateinit var value: J
        override fun add(s: CharSequence) {
            value = jstring(s)
        }

        override fun add(v: J) {
            value = v
        }

        override fun finish(): J = value
        override fun isObj(): Boolean = false
    }

    override fun arrayContext() = object : FContext<J> {
        val vs = arrayListOf<J>()
        override fun add(s: CharSequence) {
            vs.add(jstring(s))
        }

        override fun add(v: J) {
            vs.add(v)
        }

        override fun finish(): J = jarray(vs)
        override fun isObj(): Boolean = false
    }

    override fun objectContext() = object : FContext<J> {
        var key: String? = null
        val vs = mutableMapOf<String, J>()
        override fun add(s: CharSequence): Unit =
                if (key == null) {
                    key = s.toString()
                } else {
                    val k = key
                    if (k != null) vs[k] = jstring(s)
                    key = null
                }

        override fun add(v: J): Unit {
            val k = key
            if (k != null) vs[k] = v
            key = null
        }

        override fun finish(): J = jobject(vs)
        override fun isObj(): Boolean = true
    }
}