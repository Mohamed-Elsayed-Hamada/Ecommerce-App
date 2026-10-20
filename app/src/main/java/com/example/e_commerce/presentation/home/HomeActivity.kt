//package com.example.e_commerce.presentation.home
//
//import com.example.e_commerce.R
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.ui.setupWithNavController
//import com.example.e_commerce.databinding.ActivityHomeBinding
//import dagger.hilt.android.AndroidEntryPoint@AndroidEntryPoint
//class HomeActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityHomeBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityHomeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(
//                R.id.nav_host_fragment
//            ) as NavHostFragment
//
//        val navController = navHostFragment.navController
//
//
//        binding.bottomNavigation.setupWithNavController(navController)
//    }
//}