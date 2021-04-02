package com.example.fotografpaylasmafirebase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fotografpaylasmafirebase.R
import com.example.fotografpaylasmafirebase.adapter.PostRecyclerAdapter
import com.example.fotografpaylasmafirebase.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var postRecyclerAdapter: PostRecyclerAdapter

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        getData()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        postRecyclerAdapter = PostRecyclerAdapter(postList)
        recyclerView.adapter = postRecyclerAdapter
    }

    private fun getData() {

        //addSnapshotListener ile veriler gerçek zamanlı olarak okunur bir güncelleme durumunda tekrar haber verir.
        //Tek bir kullanıcının postlarını görmek istersek collection("Post").whereEqualTo(..) şeklinde ilgili email adresine ait postları getirebilirdik.
        //Postları date e göre descending olacak şekilde sıralıyoruz.
        database.collection("Post").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                } ?: kotlin.run {
                    snapshot?.let {
                        if (!snapshot.isEmpty) {
                            val documentList = snapshot.documents
                            postList.clear()
                            for (document in documentList) {
                                val comment = document.get("comment") as String
                                val email = document.get("email") as String
                                val imageUrl = document.get("imageUrl") as String
                                postList.add(
                                    Post(
                                        email = email,
                                        comment = comment,
                                        imageUrl = imageUrl
                                    )
                                )
                            }
                            postRecyclerAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sharePhoto -> {
                val intent = Intent(this, SharingPhotoActivity::class.java)
                startActivity(intent)
            }
            R.id.signOut -> {
                auth.signOut()
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
