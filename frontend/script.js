let startTime;
let currentIndex = 0;

const testTexts = [
  "I told my computer I needed a break, and now it will stop sending me beach photos.",
  "Why skeletons do not fight each other? They do not have the guts.",
  "Typing this fast makes me feel like a caffeinated octopus on a keyboard.",
  "He threw his clock out the window to see time fly but now it is broken and he is late.",
  "I asked my dog what is two minus two. He said nothing.",
  "This sentence has no purpose except to confuse and entertain you rapidly.",
  "The programmer quit his job because he did not get arrays.",
  "I saw a typo in a horror story once it was truly terrifying.",
  "Typing tricky texts totally trains terrific typing techniques.",
  "She sells sea shells by the seashore, but surely she should ship shorter shells?"
];

const shuffledTexts = [...testTexts].sort(() => Math.random() - 0.5);

function normalize(text) {
  return text.trim().replace(/\s+/g, " ").toLowerCase();
}

function calculateAccuracy(original, typed) {
  let correctChars = 0;
  const minLength = Math.min(original.length, typed.length);

  for (let i = 0; i < minLength; i++) {
    if (original[i] === typed[i]) {
      correctChars++;
    }
  }

  return (correctChars / original.length) * 100;
}

function highlightMistakes(original, typed) {
  let highlighted = "";

  for (let i = 0; i < original.length; i++) {
    if (typed[i] === undefined) {
      highlighted += `<span style="background-color: lightgray;">_</span>`;
    } else if (original[i] === typed[i]) {
      highlighted += `<span style="color: green;">${original[i]}</span>`;
    } else {
      highlighted += `<span style="color: red;">${typed[i]}</span>`;
    }
  }

  return highlighted;
}

function startTest() {
  if (currentIndex >= shuffledTexts.length) {
    alert("You've completed all the sentences! Great job!");
    return;
  }

  const input = document.getElementById("input");
  const testText = shuffledTexts[currentIndex];

  input.value = "";
  input.disabled = false;
  input.focus();

  document.getElementById("test-text").innerText = testText;
  document.getElementById("time").innerText = "Time: -- seconds";
  document.getElementById("speed").innerText = "Speed: -- WPM";
  document.getElementById("accuracy").innerText = "Accuracy: -- %";
  document.getElementById("mistakes").innerHTML = "";

  startTime = new Date();
}

document.getElementById("input").addEventListener("input", function () {
  const inputText = this.value;
  const testText = document.getElementById("test-text").innerText;

  if (inputText.length >= testText.length) {
    const endTime = new Date();
    const timeTaken = (endTime - startTime) / 1000;

    const accuracy = calculateAccuracy(testText, inputText);
    const mistakesHTML = highlightMistakes(testText, inputText);

    fetch("http://localhost:8080/calculate", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `text=${encodeURIComponent(testText)}&time=${timeTaken}`
    })
      .then(response => response.json())
      .then(data => {
        document.getElementById("time").innerText = `Time: ${data.time.toFixed(2)} seconds`;

        if (!isNaN(data.wpm)) {
          document.getElementById("speed").innerText = `Speed: ${data.wpm.toFixed(2)} WPM`;
        } else {
          document.getElementById("speed").innerText = "Speed: Could not calculate";
        }

        document.getElementById("accuracy").innerText = `Accuracy: ${accuracy.toFixed(2)} %`;
        document.getElementById("mistakes").innerHTML = `Mistakes:<br>${mistakesHTML}`;

        document.getElementById("input").disabled = true;
        currentIndex++;
      })
      .catch(err => console.error("Error:", err));
  }
});

document.getElementById("input").addEventListener("paste", function (e) {
  e.preventDefault();
  alert("Pasting is disabled in this typing test. Please type manually.");
});

