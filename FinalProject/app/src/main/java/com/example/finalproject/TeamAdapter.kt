package com.example.finalproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.finalproject.R

class TeamAdapter(
    private val teams: List<String>, // Takım isimleri
    private val teamLogoUrls: Map<String, String>, // Takım logolarının URL'leri
    private val onTeamClick: (String) -> Unit // Tıklama olayı için callback
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_team, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val teamName = teams[position]
        val logoUrl = teamLogoUrls[teamName]
        holder.bind(teamName, logoUrl)
    }

    override fun getItemCount(): Int = teams.size

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val teamNameTextView: TextView = itemView.findViewById(R.id.tvTeamName)
        private val teamLogoImageView: ImageView = itemView.findViewById(R.id.ivTeamLogo)

        fun bind(teamName: String, logoUrl: String?) {
            teamNameTextView.text = teamName
            teamLogoImageView.load(logoUrl) {
                placeholder(R.drawable.besiktas_logo) // Yüklenirken
                error(R.drawable.fenerbahce_logo)    // Hata durumunda
            }

            // Tıklama olayı için animasyon ekle
            itemView.setOnClickListener {
                // Ölçeklendirme (scale_up) animasyonu başlat
                val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_up)
                itemView.startAnimation(animation)

                // Animasyon bittikten sonra tıklama callback'i çalıştır
                animation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                    override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                    override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                        onTeamClick(teamName) // Callback'i tetikle
                    }

                    override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                })
            }
        }
    }
}
