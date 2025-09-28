// Super Admin Project Approval/Reject Logic

const API_BASE = "http://localhost:8080"; // Change if your backend runs elsewhere

document.addEventListener("DOMContentLoaded", () => {
  const projectList = document.getElementById("projectList");

  // Fetch pending projects
  fetch(`${API_BASE}/api/projects/admin/approval-status/PENDING`, {
    headers: {
      Authorization: "Bearer " + (localStorage.getItem("accessToken") || ""),
    },
  })
    .then((res) => res.json())
    .then((data) => {
      if (!data.data || !Array.isArray(data.data)) {
        projectList.innerHTML = "<p>No pending projects found.</p>";
        return;
      }
      projectList.innerHTML = "";
      data.data.forEach((project) => {
        const div = document.createElement("div");
        div.className = "border rounded p-4 mb-4 bg-white shadow";
        div.innerHTML = `
          <h3 class="font-bold text-lg mb-2">${project.title}</h3>
          <p><strong>Partner:</strong> ${project.partner}</p>
          <p><strong>County:</strong> ${project.county}</p>
          <p><strong>Status:</strong> ${project.approvalStatus}</p>
          <button class="approve-btn bg-green-600 text-white px-3 py-1 rounded mr-2" data-id="${project.id}">Approve</button>
          <button class="reject-btn bg-red-600 text-white px-3 py-1 rounded" data-id="${project.id}">Reject</button>
          <div class="reject-reason mt-2 hidden">
            <input type="text" placeholder="Reason for rejection" class="border px-2 py-1 rounded w-64" />
            <button class="send-reject bg-red-700 text-white px-2 py-1 rounded ml-2" data-id="${project.id}">Send</button>
          </div>
        `;
        projectList.appendChild(div);
      });
    });

  // Approve/Reject handlers
  projectList.addEventListener("click", function (e) {
    if (e.target.classList.contains("approve-btn")) {
      const id = e.target.getAttribute("data-id");
      fetch(`${API_BASE}/api/projects/admin/approve/${id}`, {
        method: "POST",
        headers: {
          Authorization:
            "Bearer " + (localStorage.getItem("accessToken") || ""),
          "Content-Type": "application/json",
        },
      })
        .then((res) => res.json())
        .then(() => location.reload());
    }
    if (e.target.classList.contains("reject-btn")) {
      const parent = e.target.closest("div");
      parent.querySelector(".reject-reason").classList.remove("hidden");
    }
    if (e.target.classList.contains("send-reject")) {
      const id = e.target.getAttribute("data-id");
      const parent = e.target.closest("div");
      const reason = parent.querySelector("input").value;
      fetch(`${API_BASE}/api/projects/admin/reject/${id}`, {
        method: "POST",
        headers: {
          Authorization:
            "Bearer " + (localStorage.getItem("accessToken") || ""),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ reason }),
      })
        .then((res) => res.json())
        .then(() => location.reload());
    }
  });
});
