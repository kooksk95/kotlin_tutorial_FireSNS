package com.example.firesns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firesns.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*
import java.sql.Timestamp

private const val TAG = "FeedActivity"
class FeedActivity : AppCompatActivity() {

    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adaptor: PostAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        posts = mutableListOf() // 1. data source
        adaptor = PostAdaptor(this, posts)

        // 2. adaptor - layout manager를 RecyclerView에 바인딩
        rvPost.adapter = adaptor
        rvPost.layoutManager = LinearLayoutManager(this)

        firestoreDb = FirebaseFirestore.getInstance()
//        val settings = FirebaseFirestoreSettings.Builder()
//            .setTimestampsInSnapshotsEnabled(true)
//            .build()
//        firestoreDb.setFirestoreSettings(settings)
//        Timestamp ts = snapshot.getTimestamp("created_at")
//        java.util.Date date = ts.toDate()

        val postsReference = firestoreDb
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
        // addSnapshotListener를 통해 update 내역을 자동으로 반영할 수 있다.
        // ? 모든 쿼리를 다시 날리는 건가?
        postsReference.addSnapshotListener { snapshot, exception ->
            if(exception != null || snapshot == null){
                Log.e(TAG, "피드 쿼리 과정에서 오류가 발생했습니다.", exception)
                return@addSnapshotListener
            }
            var postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adaptor.notifyDataSetChanged()
            for(post in postList){
                Log.i(TAG, "Post $post")
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_feed, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_profile) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}