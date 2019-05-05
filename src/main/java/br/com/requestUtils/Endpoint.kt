package br.com.requestUtils

import android.app.Activity
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import br.com.requestUtils.RequestUtils.get
import java.util.*

data class Config(
    val urlBase:String="",
    var hashMap: HashMap<String,String>
)


class Endpoint{
    lateinit var config : MutableList<Config>
    inline fun <reified T> request(path:String, t:T):MutableLiveData<T>{
        var livedata = MutableLiveData<T>()
        livedata.javaClass
        //get(config[0].urlBase+"/".replace("//","/")+path,livedata )
        return livedata

    }
}
