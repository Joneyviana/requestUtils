package br.com.requestUtils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import java.util.*
data class Config(
    val urlBase:String="",
    var hashMap: HashMap<String,String>
)

class SimpleRequest(var urlBase: String) {
    var erro = MutableLiveData<Boolean>()
    var erroContent  = MutableLiveData<String>()
    fun  post(complemento:String?,content:Any):MutableLiveData<String>{
        var result = MutableLiveData<String>();
        post(getUrlComplete(complemento),content,{
            result.postValue(it)
        },{
            erro.postValue(true)
            erroContent.postValue(it)
        })
        return result
    }
    inline fun <reified T>  get(complemto:String?,livedata:MutableLiveData<T>):MutableLiveData<T>{
        _get(getUrlComplete(complemto),livedata,erro)
        return livedata
    }

    fun put(complemento: String?,content: Any):MutableLiveData<String>{
        var result = MutableLiveData<String>();
        put(getUrlComplete(complemento),content,{
            result.postValue(it)
        },{
            erro.postValue(true)
            erroContent.postValue(it)
        })
        return result
    }

    fun get(complemento: String?):LiveData<String>{
        var livedata = MutableLiveData<String>()
        Log.i("Entrou all complemento", "yedy")
        RequestUtils.get(getUrlComplete(complemento),{content:String? -> livedata.postValue(content)})
        return livedata
    }
    fun getUrlComplete(complemento:String?):String{
        var urlCompleta = urlBase
        if(complemento != null && !complemento.isEmpty()){
            urlCompleta = urlBase+"/".replace("//","/")+complemento
        }
        Log.i("URL completa",urlCompleta)
        //Log.i("complemento",complemento)
        return urlCompleta
    }
}
