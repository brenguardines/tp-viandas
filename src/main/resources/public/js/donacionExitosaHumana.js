document.addEventListener('DOMContentLoaded', (event) => {
    // Selecciona los botones usando sus IDs
    const botonDonacion = document.getElementById("donacionExitosaBoton");

    // Añade eventos 'click' a los botones
    botonDonacion.addEventListener('click', () => {
        window.location.href = '/formaDeContribucionHumana'; // Reemplaza con la URL correspondiente
    });
});