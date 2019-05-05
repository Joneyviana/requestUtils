package br.com.requestUtils

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import com.google.gson.GsonBuilder



data class FileUpload(val name:String,val path : String, val progress : RequestBody? = null)


object RequestUtils {
    var status_message = hashMapOf<Int,String>()
    var conexao_failed = "Verifique sua conexão"
    var client: OkHttpClient = OkHttpClient()
    var baseRequest = Request.Builder()
    var JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!
    var gson = GsonBuilder()
        .setLenient()
        .create()

    fun addHeader(key:String, value : String ){
        baseRequest.addHeader(key,value)
    }

    inline fun <reified T> get(url: String,liveData: MutableLiveData<T>,failed: MutableLiveData<Boolean>?=null){
        baseRequest.url(url).get()
        startRequest(liveData,failed)
    }

    inline fun <reified T> get(url: String,liveData: MutableLiveData<T>,request:Request.Builder,failed: MutableLiveData<Boolean>?=null){
        request.url(url).get()
        startRequest(liveData,request,failed)
    }

    fun  get(url: String,success : (String?) -> Unit, failed: (String?) -> Unit={}){
        baseRequest.url(url).get()
        startRequest(success,failed)
    }

    inline fun <reified T> post(url: String, requestObject:Any, liveData: MutableLiveData<T>,failed: MutableLiveData<Boolean>?=null) {
        baseRequest.url(url)
            .post(getBody(requestObject))
        startRequest(liveData,failed)
    }

    inline fun <reified T> upload(url:String,requestObject:Any, file:FileUpload, liveData: MutableLiveData<T>,failed: MutableLiveData<Boolean>?=null){

        baseRequest.url(url).post(prepareMultipart(requestObject,file))

        startRequest(liveData,failed)
    }

    fun upload(url:String,requestObject:Any, file:FileUpload, success : (String?) -> Unit, failed: (String?) -> Unit={}){
        Log.i("passed","inicia aqui")
        baseRequest.url(url).post(prepareMultipart(requestObject,file))
        Log.i("passed","chegou aqui")
        startRequest(success,failed)
    }

    fun prepareMultipart(requestObject:Any, file:FileUpload): MultipartBody{
        var multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        multipartBody?.addPart(getBody(requestObject))
        multipartBody.addFormDataPart(file.name,file.path,file.progress)
        Log.i("passed","multipart")
        return multipartBody.build()
    }

    fun getBody(requestObject:Any):RequestBody{
        val json = gson.toJson(requestObject)
        Log.i("teste do body",json)
        return RequestBody.create(JSON, json)
    }

    fun  post(url: String,requestObject:Any, success : (String?) -> Unit, failed: (String?) -> Unit={}) {
        baseRequest.url(url).post(getBody(requestObject))
        startRequest(success,failed)
    }

    fun put(url: String,requestObject:Any, success : (String?) -> Unit, failed: (String?) -> Unit={}){
        baseRequest.url(url).put(getBody(requestObject))
        startRequest(success,failed)
    }

    inline fun <reified T> put(url: String,requestObject:Any,liveData: MutableLiveData<T>,failed: MutableLiveData<Boolean>?=null){
        baseRequest.url(url).put(getBody(requestObject))
        startRequest(liveData,failed)
    }

    inline fun <reified T> startRequest(liveData: MutableLiveData<T>,failed:MutableLiveData<Boolean>? = null){
        Log.i("passed","chegou aqui tbm")
        client.newCall(baseRequest.build()).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                var content = response.body()?.bytes()?.inputStream()?.bufferedReader().use { it?.readText() }
                Log.i("teste",content)
                if(response!!.code() < 300 && content != "") {
                    liveData.postValue(gson.fromJson<T>(content,object: TypeToken<T>(){}.type))
                }
                else if (failed != null){
                    failed?.postValue(true)
                }

            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.i("EXCEPTION",e.toString())
                if(failed != null)
                    failed?.postValue(true)
            }
        })
    }

    inline fun <reified T> startRequest(liveData: MutableLiveData<T>,request:Request.Builder,failed:MutableLiveData<Boolean>? = null){
        Log.i("passed","chegou aqui tbm")
        client.newCall(request.build()).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                var content = response.body()?.bytes()?.inputStream()?.bufferedReader().use { it?.readText() }
                Log.i("teste",content)
                if(response!!.code() < 300 && content != "") {
                    liveData.postValue(gson.fromJson<T>(content,object: TypeToken<T>(){}.type))
                }
                else if (failed != null){
                    failed?.postValue(true)
                }

            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.i("EXCEPTION",e.toString())
                if(failed != null)
                    failed?.postValue(true)
            }
        })
    }

    fun  startRequest(success : (String?) -> Unit,failed: (String?) -> Unit = {}){
        client.newCall(baseRequest.build()).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                var content = response.body()?.bytes()?.inputStream()?.bufferedReader().use { it?.readText() }
                Log.i("teste",response!!.code().toString())
                Log.i("dfsdfsdgdfg", content+"ddfdfdf")
                if(response!!.code() < 300) {
                    success(content)
                }
                else if (failed != {}) {
                    if(response!!.code() in status_message.keys) {
                        failed(status_message[response!!.code()])
                    }
                    else {
                        failed(content)
                    }
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.i("EXCEPTION",e.toString())
                if(failed != {}) {
                    val message = e.toString()
                    if(message.contains("java.net")){
                        failed(conexao_failed)
                    }
                    else {
                        failed(e.toString())
                    }
                }
            }
        })
    }

}

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json,object: TypeToken<T>(){}.type)
