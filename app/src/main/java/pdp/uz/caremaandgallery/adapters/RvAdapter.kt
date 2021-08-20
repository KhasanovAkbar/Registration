package pdp.uz.caremaandgallery.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pdp.uz.caremaandgallery.R
import pdp.uz.caremaandgallery.databinding.ItemViewBinding
import pdp.uz.caremaandgallery.models.User
import java.io.File

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RvAdapter(var userList: ArrayList<User>, var onMyItemClick: OnMyItemClick) :
    RecyclerView.Adapter<RvAdapter.Vh>() {

    inner class Vh(var itemViewBinding: ItemViewBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        fun onBind(user: User, position: Int) {
            if (user.image == null) {
                itemViewBinding.image.setImageResource(R.drawable.ic_baseline_person_24)

            } else {
                itemViewBinding.image.setImageURI(Uri.fromFile(File(user.image)))
            }
            itemViewBinding.nameTv.text = user.name
            itemViewBinding.phoneNumberTv.text = user.phoneNumber

            itemViewBinding.cardView.setOnClickListener {
                onMyItemClick.onItemClick(user, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemViewBinding.inflate(LayoutInflater.from(parent.context), null, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(userList[position], position)
    }

    override fun getItemCount(): Int = userList.size

    interface OnMyItemClick {
        fun onItemClick(user: User, position: Int)
    }
}