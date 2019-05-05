package br.com.requestUtils

import android.arch.lifecycle.MutableLiveData
import org.junit.Test

import org.junit.Assert.*

class EndpointTest {

    @Test
    fun getConfig() {
    }

    @Test
    fun setConfig() {
    }

    @Test
    fun request(){
        assertEquals(MutableLiveData<String>().javaClass ,Endpoint.request("qualquer coisa","teste").javaClass)
        assertEquals(MutableLiveData<String>(). ,Endpoint.request("qualquer coisa",String::class.java).o)
    }


}