package ubaidillah.qowwim.aplikasi_x0b_2c

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MainActivity  : AppCompatActivity(), View.OnClickListener {
    lateinit var mhsAdapter: AdapterDataMhs
    lateinit var mediaHelper: MediaHelper
    lateinit var prodiAdapter : ArrayAdapter<String>
    var daftarMhs = mutableListOf<HashMap<String, String>>()
    var daftarProdi = mutableListOf<String>()
    val root = "http://192.168.43.216"
    val BASE_URL = "$root/Qowwim/AndroidPertemuan14/web_api/show_data.php"
    val BASE_URL2 = "$root/Qowwim/AndroidPertemuan14/web_api/get_nama_prodi.php"
    val BASE_URL3 = "$root/Qowwim/AndroidPertemuan14/web_api/queryInsertUpdateDelete.php"
    var namafile = ""
    var fileUri = Uri.parse("")
    var imStr = ""
    var pilihProdi = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        mhsAdapter = AdapterDataMhs(daftarMhs)
        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mhsAdapter = AdapterDataMhs(daftarMhs,this) //new
        mediaHelper = MediaHelper()
        listMhs.layoutManager = LinearLayoutManager(this)
        listMhs.adapter = mhsAdapter

        prodiAdapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,daftarProdi)
        spinProdi.adapter = prodiAdapter
        spinProdi.onItemSelectedListener = itemSelected

        imUpload.setOnClickListener(this)
//        listMhs.addOnItemTouchListener(itemTouch) #new
        btnInsert.setOnClickListener(this)
        btnUpdate.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnFind.setOnClickListener(this)
    }
    val itemSelected = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            spinProdi.setSelection(0)
            pilihProdi = daftarProdi.get(0)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihProdi = daftarProdi.get(position)
        }
    }
    fun requestPermission() = runWithPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    ) {
        fileUri = mediaHelper.getOutputMediaFileUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(intent, mediaHelper.getRcCamera())
    }
    //
    fun showDataMhs(namaMhs: String){
        val request = object : StringRequest(
                Request.Method.POST,BASE_URL,
                Response.Listener { response ->
                    daftarMhs.clear()
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var mhs = HashMap<String,String>()
                        mhs.put("nim",jsonObject.getString("nim"))
                        mhs.put("nama",jsonObject.getString("nama"))
                        mhs.put("nama_prodi",jsonObject.getString("nama_prodi"))
                        mhs.put("url",jsonObject.getString("url"))
                        mhs.put("alamat",jsonObject.getString("alamat"))
                        daftarMhs.add(mhs)
                    }
                    mhsAdapter.notifyDataSetChanged()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this,"Terjadi kesalahan koneksi ke server", Toast.LENGTH_SHORT).show()
                }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("nama",namaMhs)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == mediaHelper.getRcCamera()) {
                imStr = mediaHelper.getBitmapToString(imUpload, fileUri)
                namafile = mediaHelper.getMyFileName()
            }
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imUpload ->{
                requestPermission()
            }
            R.id.btnInsert ->{
                queryInsertUpdateDelete("insert")
            }
            R.id.btnUpdate ->{
                queryInsertUpdateDelete("update")
            }
            R.id.btnDelete ->{
                queryInsertUpdateDelete("delete")
            }
            R.id.btnFind ->{
                showDataMhs(edNamaMhs.text.toString().trim())
            }
        }
    }
    //
    fun queryInsertUpdateDelete(mode : String){
        val request = object : StringRequest(
                Method.POST,BASE_URL3,
                Response.Listener { response ->
                    Log.i("info","["+response+"]")
                    val jsonObject = JSONObject(response)
                    val error = jsonObject.getString("kode")
                    if(error.equals("000")){
                        Toast.makeText(this,"Operasi berhasil", Toast.LENGTH_LONG).show()
                        showDataMhs("")
                    }else{
                        Toast.makeText(this,"Operasi GAGAL", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
                }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                val nmFile = "DC"+ SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                        .format(Date())+".jpg"
                when(mode){
                    "insert" ->{
                        hm.put("mode","insert")
                        hm.put("nim",edNim.text.toString())
                        hm.put("nama",edNamaMhs.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",nmFile)
                        hm.put("nama_prodi",pilihProdi)
                        hm.put("alamat",edAlamat.text.toString())
                    }
                    "update" ->{
                        hm.put("mode","update")
                        hm.put("nim",edNim.text.toString())
                        hm.put("nama",edNamaMhs.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",nmFile)
                        hm.put("nama_prodi",pilihProdi)
                        hm.put("alamat",edAlamat.text.toString())
                    }
                    "delete" ->{
                        hm.put("mode","delete")
                        hm.put("nim",edNim.text.toString())
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun getNamaProdi(){
        val request = StringRequest(
                Request.Method.POST,BASE_URL2,
                Response.Listener { response ->
                    daftarProdi.clear()
                    Log.d("WWWWWW",response)
                    val jsonArray = JSONArray(response)
                    for(x in 0 .. (jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        daftarProdi.add(jsonObject.getString("nama_prodi"))
                    }
                    prodiAdapter.notifyDataSetChanged()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this,"Terjadi kesalahan koneksi ke server", Toast.LENGTH_SHORT).show()
                }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
    override fun onStart(){
        super.onStart()
        showDataMhs("")
        getNamaProdi()
    }
}