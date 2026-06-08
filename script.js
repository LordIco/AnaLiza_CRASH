const observer = new IntersectionObserver((entries) => {
  entries.forEach((entry) => {
    if (entry.isIntersecting) entry.target.classList.add("visible");
  });
}, { threshold: 0.14 });

document.querySelectorAll(".reveal").forEach((el) => observer.observe(el));

// CTA focus glow
document.querySelectorAll(".btn").forEach((btn) => {
  btn.addEventListener("mousemove", (event) => {
    const rect = btn.getBoundingClientRect();
    btn.style.setProperty("--x", `${event.clientX - rect.left}px`);
    btn.style.setProperty("--y", `${event.clientY - rect.top}px`);
  });
});
