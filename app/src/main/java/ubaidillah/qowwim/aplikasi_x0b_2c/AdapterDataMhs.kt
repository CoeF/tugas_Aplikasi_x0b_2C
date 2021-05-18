package ubaidillah.qowwim.aplikasi_x0b_2c
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class AdapterDataMhs(val datamhs : List<HashMap<String,String>>,
                     val mainActivity: MainActivity
) :
    RecyclerView.Adapter<AdapterDataMhs.HolderDataMhs>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HolderDataMhs {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.baris_mahasiswa,p0, false)
        return HolderDataMhs(v)
    }

    override fun getItemCount(): Int {
        return datamhs.size
    }
    override fun onBindViewHolder(p0: HolderDataMhs, p1: Int) {
        val data = datamhs.get(p1)
        p0.txNim.setText(data.get("nim"))
        p0.txNama.setText(data.get("nama"))
        p0.txProdi.setText(data.get("nama_prodi"))
        p0.txAlamat.setText(data.get("alamat"))
        if (!data.get("url").equals(""))
            Picasso.get().load(data.get("url")).into(p0.photo);

        if (p1.rem(2) == 0) p0.cLayout.setBackgroundColor(
            Color.rgb(230, 245, 240)
        )
        else p0.cLayout.setBackgroundColor(Color.rgb(255, 255, 245))

        p0.cLayout.setOnClickListener(View.OnClickListener {
            val pos = mainActivity.daftarProdi.indexOf(data.get("nama_prodi"))
            mainActivity.spinProdi.setSelection(pos)
            mainActivity.edNim.setText(data.get("nim"))
            mainActivity.edNamaMhs.setText(data.get("nama"))
            mainActivity.edAlamat.setText(data.get("alamat"))
            Picasso.get().load(data.get("url")).into(mainActivity.imUpload)
        })

        if (!data.get("url").equals(""))
            Picasso.get().load(data.get("url")).into(p0.photo);
    }
    class HolderDataMhs(v : View) : RecyclerView.ViewHolder(v){
        val txNim = v.findViewById<TextView>(R.id.txNim)
        val txNama = v.findViewById<TextView>(R.id.txNama)
        val txProdi = v.findViewById<TextView>(R.id.txProdi)
        val photo = v.findViewById<ImageView>(R.id.imageView)
        val txAlamat = v.findViewById<TextView>(R.id.txAlamat)
        val cLayout = v.findViewById<ConstraintLayout>(R.id.cLayout) //new
    }
}