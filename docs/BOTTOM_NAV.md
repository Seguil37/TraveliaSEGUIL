# BottomNavView – Contrato de integración

El componente `BottomNavView` concentra la navegación principal de la app. Para mantener un comportamiento coherente en todas las pantallas:

1. **Configura la pestaña actual desde XML**
   ```xml
   <com.proyecto.travelia.ui.BottomNavView
       android:id="@+id/bottom_nav"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       app:currentTab="explorar" />
   ```
   El atributo `app:currentTab` asegura que la Activity arranque con el tab correcto incluso antes de enlazar la lógica.

2. **Vincula el componente en `onCreate`**
   ```java
   BottomNavView bottom = findViewById(R.id.bottom_nav);
   FavoritesRepository favorites = new FavoritesRepository(this);
   ReservationsRepository reservations = new ReservationsRepository(this);
   BottomNavComponent.bind(this, bottom, BottomNavView.Tab.EXPLORAR,
           favorites, reservations);
   ```
   `BottomNavComponent` se encarga de:
   - Conectar `BottomNavView` con un `BottomNavNavigationController` compartido (`ActivityBottomNavController`).
   - Observar los `LiveData` de `FavoritesRepository` y `ReservationsRepository` para actualizar automáticamente los badges.
   - Aplicar el estado seleccionado (`highlight`) desde el `LiveData` de navegación.

3. **Uso opcional de Jetpack Navigation**

   `BottomNavView` expone `bindToNavController(LifecycleOwner, BottomNavNavigationController)` para que puedas reemplazar el controlador por uno basado en `NavController` de Jetpack o en un `StateFlow`. Solo necesitas implementar la interfaz `BottomNavNavigationController` y pasarla al `bind` anterior.

4. **Badges/contadores**

   Utiliza `setBadgeCount(Tab tab, int count)` para actualizar manualmente cualquier pestaña. Cuando los repositorios arrojan `0`, el badge se oculta automáticamente. Los contadores admiten formato "99+" para valores grandes.

5. **Botón ADD**

   Personaliza la acción central con `setOnAddClickListener`. Si no se registra ningún listener, el clic se propagará al controlador de pestañas como un tab más.

Con este flujo cada Activity solo necesita instanciar los repositorios y llamar a `BottomNavComponent.bind(...)`. La navegación queda centralizada y los contadores se mantienen sincronizados sin código duplicado.
