const api = '';
let session = JSON.parse(localStorage.getItem('session') || 'null');

const statusEl = document.querySelector('#status');
const sessionInfo = document.querySelector('#sessionInfo');
const projectsList = document.querySelector('#projectsList');
const freelancersList = document.querySelector('#freelancersList');
const postulacionesList = document.querySelector('#postulacionesList');

function headers() {
    const base = { 'Content-Type': 'application/json' };
    if (session?.token) base.Authorization = 'Bearer ' + session.token;
    return base;
}

async function request(path, options = {}) {
    const response = await fetch(api + path, { ...options, headers: { ...headers(), ...(options.headers || {}) } });
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;
    if (!response.ok) throw new Error(data?.error || 'Error HTTP ' + response.status);
    return data;
}

function setStatus(message, isError = false) {
    statusEl.textContent = message;
    statusEl.className = isError ? 'status error' : 'status';
}

function updateSessionView() {
    sessionInfo.textContent = session ? `${session.username} (${session.rol})` : 'Sin sesion';
    document.querySelector('#clientePanel').classList.toggle('hidden', session?.rol !== 'CLIENTE');
    document.querySelector('#freelancerPanel').classList.toggle('hidden', session?.rol !== 'FREELANCER');
}

function formData(form) {
    return Object.fromEntries(new FormData(form).entries());
}

document.querySelector('#registerForm').addEventListener('submit', async event => {
    event.preventDefault();
    try {
        await request('/api/auth/register', { method: 'POST', body: JSON.stringify(formData(event.target)) });
        setStatus('Cuenta creada. Ahora inicia sesion.');
        event.target.reset();
    } catch (error) { setStatus(error.message, true); }
});

document.querySelector('#loginForm').addEventListener('submit', async event => {
    event.preventDefault();
    try {
        session = await request('/api/auth/login', { method: 'POST', body: JSON.stringify(formData(event.target)) });
        localStorage.setItem('session', JSON.stringify(session));
        setStatus('Sesion iniciada.');
        updateSessionView();
        await loadAll();
    } catch (error) { setStatus(error.message, true); }
});

document.querySelector('#logoutBtn').addEventListener('click', () => {
    session = null;
    localStorage.removeItem('session');
    updateSessionView();
    setStatus('Sesion cerrada.');
});

document.querySelector('#projectForm').addEventListener('submit', async event => {
    event.preventDefault();
    const data = formData(event.target);
    data.presupuesto = Number(data.presupuesto);
    try {
        await request('/api/proyectos', { method: 'POST', body: JSON.stringify(data) });
        event.target.reset();
        await loadProjects();
        setStatus('Proyecto publicado.');
    } catch (error) { setStatus(error.message, true); }
});

document.querySelector('#profileForm').addEventListener('submit', async event => {
    event.preventDefault();
    const data = formData(event.target);
    data.tarifaHora = data.tarifaHora ? Number(data.tarifaHora) : null;
    try {
        await request('/api/usuarios/perfil', { method: 'PUT', body: JSON.stringify(data) });
        await loadFreelancers();
        setStatus('Perfil actualizado.');
    } catch (error) { setStatus(error.message, true); }
});

document.querySelector('#postulacionesForm').addEventListener('submit', async event => {
    event.preventDefault();
    try {
        const { proyectoId } = formData(event.target);
        const items = await request('/api/postulaciones?proyectoId=' + encodeURIComponent(proyectoId));
        postulacionesList.innerHTML = items.map(renderPostulacion).join('') || '<p>No hay postulaciones.</p>';
    } catch (error) { setStatus(error.message, true); }
});

document.querySelector('#refreshProjects').addEventListener('click', loadProjects);

async function loadProjects() {
    const projects = await request('/api/proyectos?soloAbiertos=false');
    projectsList.innerHTML = projects.map(project => `
        <article class="item">
            <h3>#${project.id} ${project.titulo}</h3>
            <span class="badge">${project.estado}</span>
            <p>${project.descripcion}</p>
            <p><strong>Presupuesto:</strong> S/ ${project.presupuesto}</p>
            <p><strong>Cliente:</strong> ${project.cliente.username}</p>
            ${project.freelancerAsignado ? `<p><strong>Asignado:</strong> ${project.freelancerAsignado.username}</p>` : ''}
            ${session?.rol === 'FREELANCER' && project.estado === 'ABIERTO' ? `
            <form class="applyForm" data-project-id="${project.id}">
                <input name="propuestaEconomica" type="number" min="1" step="0.01" placeholder="Propuesta economica" required>
                <textarea name="cartaPresentacion" placeholder="Carta de presentacion" required></textarea>
                <button>Postularme</button>
            </form>` : ''}
        </article>`).join('') || '<p>No hay proyectos.</p>';

    document.querySelectorAll('.applyForm').forEach(form => form.addEventListener('submit', applyToProject));
}

async function applyToProject(event) {
    event.preventDefault();
    const data = formData(event.target);
    data.proyectoId = Number(event.target.dataset.projectId);
    data.propuestaEconomica = Number(data.propuestaEconomica);
    try {
        await request('/api/postulaciones', { method: 'POST', body: JSON.stringify(data) });
        setStatus('Postulacion enviada.');
        event.target.reset();
    } catch (error) { setStatus(error.message, true); }
}

function renderPostulacion(item) {
    return `<article class="item">
        <h3>#${item.id} ${item.freelancer.username}</h3>
        <span class="badge">${item.estado}</span>
        <p><strong>Proyecto:</strong> ${item.proyectoTitulo}</p>
        <p><strong>Propuesta:</strong> S/ ${item.propuestaEconomica}</p>
        <p>${item.cartaPresentacion}</p>
        ${item.estado === 'PENDIENTE' ? `<button onclick="acceptApplication(${item.id})">Aceptar</button>` : ''}
    </article>`;
}

async function acceptApplication(id) {
    try {
        await request('/api/postulaciones/' + id + '/aceptar', { method: 'PATCH' });
        setStatus('Postulacion aceptada. Las demas fueron rechazadas.');
        postulacionesList.innerHTML = '';
        await loadProjects();
    } catch (error) { setStatus(error.message, true); }
}

async function loadFreelancers() {
    const freelancers = await request('/api/usuarios/freelancers');
    freelancersList.innerHTML = freelancers.map(user => `<article class="item">
        <h3>${user.username}</h3>
        <p><strong>Email:</strong> ${user.email}</p>
        <p><strong>Tarifa:</strong> ${user.tarifaHora ? 'S/ ' + user.tarifaHora : 'Sin definir'}</p>
        <p><strong>Habilidades:</strong> ${user.habilidades || 'Sin definir'}</p>
        <p>${user.biografia || ''}</p>
    </article>`).join('') || '<p>No hay freelancers.</p>';
}

async function loadAll() {
    try {
        await loadProjects();
        await loadFreelancers();
    } catch (error) { setStatus(error.message, true); }
}

updateSessionView();
loadAll();
