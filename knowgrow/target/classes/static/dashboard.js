
const API = "http://localhost:8080/api";

const rows = document.getElementById("employeeRows");
const employeeMessage = document.getElementById("employeeMessage");

async function loadEmployees() {
    rows.innerHTML = '<tr><td colspan="4">Loading employees...</td></tr>';
    employeeMessage.textContent = "";

    try {
        const response = await fetch(`${API}/employees`);
        if (!response.ok) throw new Error("Employee API returned " + response.status);

        const employees = await response.json();
        document.getElementById("totalEmployees").textContent = employees.length;

        if (!employees.length) {
            rows.innerHTML = '<tr><td colspan="4">No employees yet. Add your first employee.</td></tr>';
            return;
        }

        rows.innerHTML = "";
        employees.forEach(employee => {
            const tr = document.createElement("tr");
            [
                employee.name,
                employee.department || "—",
                employee.role || "—",
                employee.experienceYears == null ? "—" : employee.experienceYears + " years"
            ].forEach(value => {
                const td = document.createElement("td");
                td.textContent = value;
                tr.appendChild(td);
            });
            rows.appendChild(tr);
        });
    } catch (error) {
        rows.innerHTML = '<tr><td colspan="4">Unable to load employees.</td></tr>';
        employeeMessage.textContent =
            "Check that Spring Boot is running and GET /api/employees works. " + error.message;
    }
}

function showEmployeeForm() {
    document.getElementById("employeeForm").classList.toggle("hidden");
}

document.getElementById("refreshEmployees").addEventListener("click", loadEmployees);

document.getElementById("addEmployeeForm").addEventListener("submit", async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;

    const employee = {
        name: document.getElementById("employeeName").value.trim(),
        email: document.getElementById("employeeEmail").value.trim(),
        department: document.getElementById("employeeDepartment").value.trim(),
        role: document.getElementById("employeeRole").value.trim(),
        experienceYears: Number(document.getElementById("employeeExperience").value || 0)
    };

    try {
        const response = await fetch(`${API}/employees`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(employee)
        });

        if (!response.ok) {
            const message = await response.text();
            throw new Error(message || `Request failed (${response.status})`);
        }

        document.getElementById("addEmployeeForm").reset();
        document.getElementById("formMessage").textContent = "Employee saved successfully.";
        await loadEmployees();
    } catch (error) {
        document.getElementById("formMessage").textContent =
            "Could not save employee: " + error.message;
    } finally {
        button.disabled = false;
    }
});

loadEmployees();
