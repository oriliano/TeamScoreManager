// com.example.finalproject.adapters.PlayerAdapter.kt
package com.example.finalproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.data.GuessStatus
import com.example.finalproject.data.Player

class PlayerAdapter(
    private val players: List<Player>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CORRECT = 1
        private const val VIEW_TYPE_INCORRECT = 2
        private const val VIEW_TYPE_UNANSWERED = 0
    }

    override fun getItemViewType(position: Int): Int {
        return when (players[position].guessStatus) {
            GuessStatus.CORRECT -> VIEW_TYPE_CORRECT
            GuessStatus.INCORRECT -> VIEW_TYPE_INCORRECT
            GuessStatus.UNANSWERED -> VIEW_TYPE_UNANSWERED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CORRECT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_player_correct, parent, false)
                CorrectPlayerViewHolder(view)
            }
            VIEW_TYPE_INCORRECT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_player_incorrect, parent, false)
                IncorrectPlayerViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_player_unanswered, parent, false)
                UnansweredPlayerViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val player = players[position]
        when (holder) {
            is CorrectPlayerViewHolder -> holder.bind(player)
            is IncorrectPlayerViewHolder -> holder.bind(player)
            is UnansweredPlayerViewHolder -> holder.bind(player)
        }
    }

    override fun getItemCount(): Int = players.size

    // ViewHolder for Correct Players
    class CorrectPlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlayerNameCorrect: TextView = itemView.findViewById(R.id.tvPlayerNameCorrect)
        private val tvTeamNameCorrect: TextView = itemView.findViewById(R.id.tvTeamNameCorrect)

        fun bind(player: Player) {
            tvPlayerNameCorrect.text = player.name
            tvTeamNameCorrect.text = player.team
        }
    }

    // ViewHolder for Incorrect Players
    class IncorrectPlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlayerNameIncorrect: TextView = itemView.findViewById(R.id.tvPlayerNameIncorrect)
        private val tvTeamNameIncorrect: TextView = itemView.findViewById(R.id.tvTeamNameIncorrect)

        fun bind(player: Player) {
            tvPlayerNameIncorrect.text = player.name
            tvTeamNameIncorrect.text = player.team
        }
    }

    // ViewHolder for Unanswered Players
    class UnansweredPlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlayerNameUnanswered: TextView = itemView.findViewById(R.id.tvPlayerNameUnanswered)
        private val tvTeamNameUnanswered: TextView = itemView.findViewById(R.id.tvTeamNameUnanswered)

        fun bind(player: Player) {
            tvPlayerNameUnanswered.text = player.name
            tvTeamNameUnanswered.text = player.team
        }
    }

    /**
     * Oyuncu listesindeki değişiklikleri güncellemek için kullanılan yöntem
     */
    fun updatePlayers(updatedPlayers: List<Player>) {
        notifyDataSetChanged()
    }
}
