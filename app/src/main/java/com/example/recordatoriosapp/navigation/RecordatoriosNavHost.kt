package com.example.recordatoriosapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.recordatoriosapp.ui.screens.CrearRecordatorioScreen
import com.example.recordatoriosapp.ui.screens.DetalleRecordatorioScreen
import com.example.recordatoriosapp.ui.screens.ListaRecordatoriosScreen
import com.example.recordatoriosapp.ui.screens.SettingsScreen
import com.example.recordatoriosapp.viewmodel.ListaRecordatoriosUiState
import com.example.recordatoriosapp.viewmodel.RecordatoriosViewModel
import com.example.recordatoriosapp.viewmodel.SettingsViewModel
import kotlinx.serialization.Serializable
import androidx.compose.runtime.LaunchedEffect
@Serializable
data object SettingsRoute

@Composable
fun RecordatoriosNavHost(
    settingsViewModel: SettingsViewModel,
    viewModel: RecordatoriosViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = ListaRoute) {

        composable<ListaRoute> {
            val uiState by viewModel.listaUiState.collectAsStateWithLifecycle()
            ListaRecordatoriosScreen(
                uiState = uiState,
                onCrearClick = { navController.navigate(CrearRoute) },
                onRecordatorioClick = { id -> navController.navigate(DetalleRoute(id)) },
                onPreferenciasClick = { navController.navigate(SettingsRoute) }
            )
        }

        composable<CrearRoute> {
            val uiState by viewModel.crearUiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.guardado) {
                if (uiState.guardado) {
                    viewModel.resetearFormulario()
                    navController.popBackStack()
                }
            }

            CrearRecordatorioScreen(
                uiState = uiState,
                onTituloChange = { viewModel.actualizarTitulo(it) },
                onDescripcionChange = { viewModel.actualizarDescripcion(it) },
                onGuardarClick = { viewModel.guardarRecordatorio() },
                viewModel = viewModel
            )
        }

        composable<DetalleRoute> { backStackEntry ->
            val ruta = backStackEntry.toRoute<DetalleRoute>()
            val uiState by viewModel.listaUiState.collectAsStateWithLifecycle()

            val recordatorio = if (uiState is ListaRecordatoriosUiState.ConDatos) {
                (uiState as ListaRecordatoriosUiState.ConDatos).recordatorios.find { it.id == ruta.recordatorioId }
            } else null

            DetalleRecordatorioScreen(
                recordatorio = recordatorio,
                onVolverClick = { navController.popBackStack() },
                onAlternarCompletado = { viewModel.alternarCompletado(it) },
                onEliminarClick = {
                    viewModel.eliminarRecordatorio(it)
                    navController.popBackStack()
                }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                viewModel = settingsViewModel,
                onVolverClick = { navController.popBackStack() }
            )
        }
    }
}